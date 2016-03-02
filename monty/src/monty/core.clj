(ns monty.core
  (:require [clojure.set :as set]))

(defn random-door []
  (rand-int 3))

(defn won? [door board]
  (= door board))

(defn choose-another [a b]
  (-> #{0 1 2}
      (disj a)
      (disj b)
      seq
      rand-nth))

(defn choose-then-change [winner]
  (let [first-guess   (random-door)
        montys-choice (choose-another first-guess winner)]
    (choose-another first-guess montys-choice)))

(defn rand-bool []
  (= 0 (rand-int 2)))

(defn play []
  (if (rand-bool)
    [:stay   (won? (random-door) (random-door))]
    [:change (let [winner (random-door)]
               (won? (choose-then-change winner) winner))]))

(defn tally [m [action won?]]
  (if won? (update m action inc) m))

(defn stats [n]
  (reduce tally {:stay 0 :change 0} (repeatedly n play)))
