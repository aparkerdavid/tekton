(ns tekton.core
 (:require [clojure.data.json :as json]
           [incanter.core :refer [$=]]))

(defn cart-add
  [p1 p2]
  (assoc p1
   :x (+ (:x p1) (:x p2)) 
   :y (+ (:y p1) (:y p2))))

(defn jitter [pts pick-index-fn offset]
  (map-indexed (fn [pti pt]
                 (if (pick-index-fn pti)
                     (cart-add pt offset)
                     pt)) pts))

(defn jitter-perimeter [ly pick-index-fn offset]
  (assoc ly :perimeter (jitter (:perimeter ly) pick-index-fn offset)))

(def pts-in
 ;; points read in from json file outta grasshopper
 (with-open [reader (clojure.java.io/reader "pts.json")]
   (json/read reader)))
      
(defn parse-pt [pt] {:x (pt "X") :y (pt "Y")})

(def parsed-pts
  {:perimeter (map parse-pt (pts-in "perimeter"))
   :interior (map parse-pt (pts-in "interior"))})

(def a-shape
   (for [z (range 0 100 1.75)]
     {:perimeter (map #(assoc % :z z) (:perimeter parsed-pts))
      :interior (map #(assoc % :z z) (:interior parsed-pts))}))

(def my-shape
   (->> a-shape
       (map-indexed (fn [li ly]
                     (cond
                      (= 0 (mod (+ li 1) 3))
                      (jitter-perimeter ly (fn [x] (= 0 (mod (+ x 2) 3))) {:x 2 :y 2})
                      :else
                      ly)))))

;; (def my-shape
;;    (->> a-shape
;;        (map-indexed (fn [li ly]
;;                      (if (= 0 (mod (+ li 1) 3))
;;                        (jitter-perimeter ly (fn [x] (= 0 (mod (+ x 5) 3))) {:x 2 :y 2})
;;                        ly)))
;;        (map-indexed (fn [li ly]
;;                      (if (= 0 (mod (+ li 4) 6))
;;                        (jitter-perimeter ly (fn [x] (= 0 (mod (+ x 2) 3))) {:x 2 :y 2})
;;                        ly)))))

(defn update-keyed-in [xs key fn]
    (map #(update-if-keyed % key fn) xs))

(defn update-if-keyed [x key fn]
    (if (has-key x key)
      (update x key fn)
      x))

(defn has-key [x key] (boolean (key x)))

(defn clay-printer [shape x-offset y-offset z-offset]
  (->> shape
       (map (fn [ly] (concat (:perimeter ly) (:interior ly))))
       (apply concat)
       (map (fn [pt]
              (let [x (+ (:x pt) x-offset)
                    y (+ (:y pt) y-offset)
                    z (+ (:z pt) z-offset)]
                   (str "G1"
                        " X" x
                        " Y" y
                        " Z" z
                        " F1000\n"))))
       (apply str)
       (#(str "G28 X Y Z\n"
              "G90\n"
              %
              "G28 X"))))
       
(spit 
 "3dprint.gcode"
 (clay-printer my-shape 40 40 2))
