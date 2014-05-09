(ns cschuyle.nines-test
  (:require [clojure.test :refer :all]
            [cschuyle.nines :as nines]))

(deftest test-combinations
  (is (= (nines/combos-for + 2) [[1 1]]))
  (is (= (nines/combos-for - 3) [[1 2] [2 1]]))
  (is (= (nines/combos-for + 3) [[1 2]])))

(deftest test-crossproduct
  (is (= #{6 8} (nines/crossproduct + #{1 3} #{5}))))

(deftest derive-order-2-values
  (is (= (nines/order 2) #{0 1 18 81})))

(deftest solves-it
  (is (= 13 (nines/solve 5))))
