(ns tekton.util)
;; Catch-all ns for utility functions that do not deal with geometric transformation

(defn with-z [z ly]
 (zipmap
  (keys ly)
  (->>
   (vals ly)
   (map (partial map #(assoc % :z z))))))

(defn take-to-height [shape height layer-height]
  (loop [z 0
         next-layer (->> shape first (with-z z))
         rest-of-shape (drop 1 shape)
         result []]
    (if (> z height)
      result
      (recur
       (+ z layer-height)
       (->> (first rest-of-shape) (with-z (+ z layer-height)))
       (drop 1 rest-of-shape)
       (conj result next-layer)))))

(defn flatten-shape [shape]
  (->> shape
   ;; Concatenate the values of each layer
   (map (fn [ly] (apply concat (vals ly))))
   ;; Concatenate all the layers
   (apply concat)))



