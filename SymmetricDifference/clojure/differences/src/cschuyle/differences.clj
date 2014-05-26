(ns cschuyle.differences
  (:require [clojure.tools.trace :as t]))

(defn update-in-with-default [m k f d]
  (update-in m [k] #(f (if (nil? %) d %))))

(defn inc-in [m k]
  "If the key k already exists in map m, increment it.  Otherwise set it to 1"
  (update-in-with-default m k inc 0))

(defn dec-in
  "If the key k already exists in map m, decrement it.  Otherwise set it to -1"
  [m k]
  (update-in-with-default m k dec 0))

(t/deftrace symmetric-difference
  ([a] a)
  ([a & [b & more]]
     (if (empty? more)
       (as-> {} acc
             (reduce (fn [m k] (inc-in m k)) acc a)
             (reduce (fn [m k] (dec-in m k)) acc b)
             (reduce-kv (fn [l k v] (if (= v 0) l (concat l (take (Math/abs v) (repeat k))))) [] acc))
       (symmetric-difference a (apply symmetric-difference b more)))))

