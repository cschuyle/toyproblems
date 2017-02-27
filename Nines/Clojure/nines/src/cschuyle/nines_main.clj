(ns cschuyle.nines-main
  (:require [cschuyle.nines :as nines])
  (:gen-class))

(defn -main [& args]
  (prn (nines/solve (or (some-> (System/getenv "ORDER") Integer/parseInt) 9))))

(defn read-int []
  (-> (read-line) Integer/parseInt))

(defn staircase []
  (let [n (read-int)]
    (for [i (range 0 n)]
      (let [reps (+ i 1)]
        [(repeat (- n reps) \space)
         (repeat reps \#)
         \newline]))))

(defn read-row []
  (mapv #(Integer/parseInt %)
        (clojure.string/split (read-line) #"\s+")))

(defn percentage [a b]
  (if (zero? b) "infinity" (quot a b)))

(defn plus-minus []
  (let [dimension (-> (read-line) Integer/parseInt)
        values (read-row)]
    (doseq [condition [pos? neg? zero?]]
      (println (percentage (count (filter condition values)) (count values))))))

(defn diagonal-diff []
  (let [dimension (read-int)
        matrix (repeatedly dimension read-row)
        d1 (reduce + (map (fn [row index] (get row index)) matrix (range 0 dimension)))
        d2 (reduce + (map (fn [row index] (get row index)) matrix (reverse (range 0 dimension))))]
    (Math/abs (- d1 d2))))

(defn time-conversion []
  (let [[h m s] (map clojure.string/trim (clojure.string/split (read-line) #":"))
        pm? (.endsWith s "PM")
        s (clojure.string/trim (.substring s 0 (- (.length s) 2)))
        [h m s] (map #(Integer/parseInt %) [h m s])
        h (cond  (and (not pm?) (= h 12)) 0
                 (and pm? (= h 12)) 12
                 pm? (+ h 12)
                 :else-its-am h)]
    (println (format "%02d:%02d:%02d" h m s))))


