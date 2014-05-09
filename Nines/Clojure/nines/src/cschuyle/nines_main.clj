(ns cschuyle.nines-main
  (:require [cschuyle.nines :as nines])
  (:gen-class))

(defn -main [& args]
  (prn (nines/solve (or (some-> (System/getenv "ORDER") Integer/parseInt) 9))))
