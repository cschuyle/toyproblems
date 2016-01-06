(ns brackitz.core
  (:require [brackitz.dup-optimized-stack :as stack]))

(defn- begin [{:keys [stack column] :as state} beginner]
  (-> state
      (update :stack stack/push beginner)
      (update :column inc)
      (assoc :last-character beginner)))

(defn- end [{:keys [stack column] :as state} matches-beginner c]
  (if (= matches-beginner (stack/peek stack))
    (-> state (update :stack stack/pop)
        (update :column inc)
        (assoc :last-character c))
    (-> state
        (update :column inc)
        (assoc :error (str "missing begin delimiter for character '" c "'")))))

(defn- process-character
  ([{:keys [column] :as state} c]
   (case c
     \( (begin state c)
     \) (end state \( c)

     \{ (begin state c)
     \} (end state \{ c)

     \[ (begin state c)
     \] (end state \[ c)

     (-> state
         (update :column inc)
         (assoc :last-character c)))))

(defn- string->state [input]
  (reduce process-character {:stack [] :column 0} (seq input)))

(defn- valid-counts? [state]
  (and (not (:error state)) (empty? (:stack state))))

(defn valid? [input]
 "Predicate for input string validity.  For the reason for a failure, use validate instead"
  (valid-counts? (string->state input)))

(defn- validate-counts [state]
  (when-not (valid-counts? state)
    (throw (Exception. (if (:error state) (str "At column " (:column state) ", " (:error state))
                           (str "At column " (:column state) ", missing end delimiter for character '" (:last-character state) "'"))))))

(defn validate [input]
  "Validate input string.  Throw an explanatory Exception if it's not valid."
  (validate-counts (string->state input)))

(defmacro exception-message [& body]
  `(try ~@body
        "ERROR, expected an exception but got none"
        (catch Exception e#
          (.getMessage e#))))
