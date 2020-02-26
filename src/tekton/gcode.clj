(ns tekton.gcode
 (:require [tekton.util :as util]))

(defn clay-printer [shape x-offset y-offset z-offset]
  (->> shape
    util/flatten-shape
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
           "G1 Z" z-offset
           "\n"
           %
           "G28 X"))))
