(ns cschuyle.differences)

(defn vinc [m k]
  (if (nil? (get m k)) (assoc m k 1)
      (update-in m [k] inc)))

(defn vdec [m k]
  (if (nil? (get m k)) (assoc m k -1)
      (update-in m [k] dec)))

(defn symmetric-difference
  [a b]
  (as-> {} acc
        (reduce (fn [m k] (vinc m k)) acc a)
        (reduce (fn [m k] (vdec m k)) acc b)
        (reduce-kv (fn [l k v] (if (= v 0) l (concat l (take (Math/abs v) (repeat k))))) [] acc)))
