(ns tekton.geo)

(defn cart-add
  [p1 p2]
  (assoc p1
   :x (+ (:x p1) (:x p2)) 
   :y (+ (:y p1) (:y p2))))

(defn jitter [pts pick-index-fn offset]
  "Jitters a list of points by translating some of them, selected by index, by a specified vector"
  (map-indexed (fn [pti pt]
                 (if (pick-index-fn pti)
                     (cart-add pt offset)
                     pt)) pts))
