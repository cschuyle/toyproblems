(ns cschuyle.differences)

(defn inc-in [m k]
  "If the key k already exists in map m, increment it.  Otherwise set it to 1"
  (assoc m k (inc (or (get m k) 0))))

(defn dec-in
  "If the key k already exists in map m, decrement it.  Otherwise set it to -1"
  [m k]
  (assoc m k (dec (or (get m k) 0))))

(defn symmetric-difference
  ([a] a)
  ([a b]
     (as-> {} acc
           (reduce (fn [m k] (inc-in m k)) acc a)
           (reduce (fn [m k] (dec-in m k)) acc b)
           (reduce-kv (fn [l k v] (if (= v 0) l (concat l (take (Math/abs v) (repeat k))))) [] acc)))
  ([a b & more]
     (apply symmetric-difference
            (symmetric-difference (symmetric-difference a b) (first more))
            (rest more))))

