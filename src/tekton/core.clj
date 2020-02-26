(ns tekton.core
 (:require 
  [tekton.geo :as geo]
  [tekton.util :as util]
  [tekton.interop :as interop]
  [tekton.gcode :as gcode]
  [tekton.vis :as vis]))

(def jittered-layer
  (let [smooth-layer (interop/pts-from-gh)]
    (update
     smooth-layer
     :jitter
     (fn [pts] (geo/jitter pts (fn [x] (= 0 (mod (+ x 2) 3))) {:x -2 :y -2})))))

(defn jittered-layer-2 [initial-counter]
 (let [smooth-layer (interop/pts-from-gh)]
     (update
      smooth-layer
      :jitter
      (fn [pts] 
        (loop [result []
               counter initial-counter
               reverse false
               current-point (first pts)
               rest-of-points (drop 1 pts)]
          (if (empty? rest-of-points)
            result
            (recur
             ;; result
             (conj result 
               (cond (or (= counter 1) (= counter 3))
                     (geo/cart-add current-point {:x -2 :y -2})
                     (= counter 2)
                     (geo/cart-add current-point {:x -4 :y -4})
                     :else
                     current-point))
             ;; counter
             (if reverse (- counter 1) (+ counter 1))
             ;; reverse
             (if (or (<= counter 0) (>= counter 4)) (not reverse) reverse)
             ;; current-point
             (first rest-of-points)
             ;; rest-of-points
             (drop 1 rest-of-points))))))))

(def layer-pattern
  (let [smooth-layer (interop/pts-from-gh)]
   (cycle [smooth-layer smooth-layer jittered-layer])))

(def layer-pattern-2
  (let [smooth-layer (interop/pts-from-gh)]
   (cycle [smooth-layer smooth-layer (jittered-layer-2 0) smooth-layer smooth-layer (jittered-layer-2 2)])))

(def a-shape (take 10 layer-pattern))

(spit 
 "3dprint.gcode"
 (gcode/clay-printer (util/take-to-height layer-pattern 150 1.75) 40 40 1.75))

;; (vis/shape->json (util/take-to-height layer-pattern 150 1.75))

(vis/render-shape (util/take-to-height layer-pattern 150 1.75))
