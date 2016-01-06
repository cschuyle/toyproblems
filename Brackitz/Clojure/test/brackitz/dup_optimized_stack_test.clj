(ns brackitz.dup-optimized-stack-test
  (:require [clojure.test :refer :all]
            [brackitz.dup-optimized-stack :as stack]))

(deftest pop-empty-throws
  (is (thrown? IllegalStateException (stack/pop []))))

(deftest pop-nil-returns-nil
  (is (nil? (stack/pop nil))))

(deftest pops
  (is (= 42 (stack/peek (stack/pop (stack/->dup-optimized-stack [42 69]))))))

(deftest pops-duplicate
  (is (= 42 (stack/peek (stack/->dup-optimized-stack [42 42])))))

(deftest pushes-once
  (is (= 42 (stack/peek (stack/push [] 42)))))

(deftest pushes-duplicate
  (let [stack (-> [] (stack/push 42) (stack/push 42))]
    (is (= 1 (count stack))) ;; my one guilty whitebox test
    (is (= 42 (stack/peek stack)))))

(deftest pushes-twice
  (is (= 69 (stack/peek (-> [] (stack/push 42) (stack/push 69))))))

(deftest peeks-nil
  (is (nil? (stack/peek nil))))

(deftest peeks-empty
  (is (nil? (stack/peek []))))

(deftest peeks-empty (is (= 42 (stack/peek (stack/push [] 42)))))

(deftest transforms-from-plain-old-stack-with-dups
  (is (= [42 42] (-> [42 42]
                     stack/->dup-optimized-stack
                     stack/<-dup-optimized-stack))))

(deftest transforms-from-plain-old-stack
  (is (= [42 69 42 42] (-> [42 69 42 42]
                     stack/->dup-optimized-stack
                     stack/<-dup-optimized-stack))))
