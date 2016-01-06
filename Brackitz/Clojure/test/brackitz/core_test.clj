(ns brackitz.core-test
  (:require [clojure.test :refer :all]
            [brackitz.core :as core]))

(deftest counts-parens-correctly
  (is (core/valid? "()"))
  (is (core/valid? ""))
  (is (core/valid? "abc123!!")))

(deftest error-for-invalid-parens
  (is (not (core/valid? ")")))
  (is (not (core/valid? "("))))

(deftest counts-squiggles
  (is (core/valid? "{}"))
  (is (not (core/valid? "{")))
  (is (not (core/valid? "}"))))

(deftest counts-sqaure-brackets
  (is (core/valid? "[]"))
  (is (not (core/valid? "[")))
    (is (not (core/valid? "]"))))

(deftest counts-combo
  (is (core/valid? "{[]}"))
  (is (not (core/valid? "{[())]}"))))

(deftest proofread-messages
  (is (= "At column 2, missing end delimiter for character '{'" (core/exception-message (core/validate " {"))))
  (is (= "At column 3, missing begin delimiter for character ')'" (core/exception-message (core/validate "  )")))))
