(ns cschuyle.differences-test
  (:require [cschuyle.differences :refer :all]
            [clojure.test :refer :all]))

(deftest should-work
  (is (empty? (symmetric-difference [] [])))
  (is (empty? (symmetric-difference nil nil)))
  (is (empty? (symmetric-difference [:b :a] [:a :b])))
  (is (= [1 4 4 5]) (sort (symmetric-difference #{1 2 3} [2 3 4 4 5]))))
