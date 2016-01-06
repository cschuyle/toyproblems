(ns brackitz.main
  (:require [brackitz.core :as core])
  (:gen-class))

(defn -main [& args]
  (when-not (System/console) (println "Enter strings for validation, one per line.  I will tell you if it's a valid bracketed string, and if not, where the error is.  Have fun!"))
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (println (if (core/valid? line) "YES" (str  "NO, " (core/exception-message (core/validate line)))))))
