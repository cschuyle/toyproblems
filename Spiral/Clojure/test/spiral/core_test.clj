(ns spiral.core-test
  (:require [clojure.test :refer :all]
            [spiral.core :refer :all]))

(deftest rotating
  (is (= \E (rotate-right \N))))

(deftest moving-forward
  (let [prior {
               :row 1 :col 1
               }
          current (move-forward prior \N)]
      (is (= 0 (current :row)))
      (is (= 1 (current :col)))))

(deftest incrementing-one-generation
  (let [prior {:arr [blank blank blank
                     blank 1     blank
                     blank blank blank]
               :row 2 :col 2 :dir \N}
        current (next-generation prior)]
    (is (= \E (current :dir)))
    (is (= 3 (current :col)))))

(deftest computing-dimension
  (let [arr {:arr [1 2 3 4]}]
    (is (= 2 (dimension arr)))))


