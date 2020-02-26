(ns tekton.interop
 (:require
  [clojure.data.json :as json]
  [clojure.walk :as walk]
  [clojure.string :as string]))

(defn lower-case-keys
  [m]
  (let [f (fn [[k v]] (if (string? k) [(string/lower-case k) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn pts-from-gh []
  (->> (with-open
         [reader (clojure.java.io/reader "pts.json")] 
         (json/read reader))
      lower-case-keys
      walk/keywordize-keys))


