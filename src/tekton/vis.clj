(ns tekton.vis
  (:require [tekton.util :as util]
            [clojure.data.json :as json]))

(defn oblique [pt]
  "add the z coord of a point to its y coord, creating an oblique projection"
  (update pt :y #(+ % (:z pt))))

(defn unitize [pts]
  "unitize a set of pts, translating and scaling them to fit in a 1.0 x 1.0 domain"
  (let
    ;; get the x and y offsets, i.e. the lowest x and y values in the set
   [x-offset (->> pts (map :x) (apply min))
    y-offset (->> pts (map :y) (apply min))
    ;; pts-at-origin is pts moved such that the x and y offsets are zero
    pts-at-origin (for [pt pts]
                    (-> pt
                        (update :x #(- % x-offset))
                        (update :y #(- % y-offset))))
    ;; the scale-factor will be the inverse of the greatest x or y value in the set of pts-at-origin
    scale-factor (max
                  (apply max (map :x pts-at-origin))
                  (apply max (map :y pts-at-origin)))]
    ;; each point will be scaled by the scale-factor
    (for [pt pts-at-origin]
      (-> pt
          (update :x #(/ % scale-factor))
          (update :y #(/ % scale-factor))))))

(defn shape->json [shape]
  (->> shape
       util/flatten-shape
       (map oblique)
       unitize
       (map (fn [pt] [(:x pt) (:y pt)]))
       json/write-str))

(defn render-shape [shape]
  (spit "resources/vis/pts.json" (shape->json shape)))
