(ns cschuyle.differences-test
  (:require [cschuyle.differences :refer :all]
            [clojure.test :refer :all]))

(deftest should-work
  (is (empty? (symmetric-difference [] [])))
  (is (empty? (symmetric-difference nil nil)))
  (is (empty? (symmetric-difference [:b :a] [:a :b])))
  (is (= [1 4 4 5]) (sort (symmetric-difference #{1 2 3} [2 3 4 4 5]))))

(deftest larger-arity
  (is (= [:a] (symmetric-difference [:a :a :b :c] [:b :b :c] [:a :b])))
  (is (empty? (symmetric-difference [:a] [:a :b]  [:b :b :c] [:a :a :b :c]))))

(deftest non-commutative
  (is (= (sort (symmetric-difference [:a :c] [:c] [:c :c]))
         (sort (symmetric-difference [:c :c] [:c] [:a :c])))))
