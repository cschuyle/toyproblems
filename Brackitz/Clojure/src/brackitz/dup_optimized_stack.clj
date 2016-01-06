(ns brackitz.dup-optimized-stack
  (:refer-clojure :exclude [peek pop]))

;; Stack optimized for multiple adjacent repetitions of the same
;; value.  In this case a single stack frame is used for the reps
;; instead of multiple stack frames.

(defn peek [stack]
  "Use this instead of of clojure.core/peek, if your stack is the dup-optimized kind."
  (:value (clojure.core/peek stack)))

(defn push [stack c]
  "Use this instead of of clojure.core/conj, if your stack is the dup-optimized kind."
  (if (= (peek stack) c)
    (conj (clojure.core/pop stack) (update (clojure.core/peek stack) :count inc))
    (conj stack {:count 1 :value c})))

(defn pop [stack]
  "Use this instead of of clojure.core/pop, if your stack is the dup-optimized kind."
  (if (= [] stack)
    (clojure.core/pop stack)
    (if-let [top (clojure.core/peek stack)]
      (if (= 1 (:count top))
        (clojure.core/pop stack)
        (conj (clojure.core/pop stack) (update top :count dec)))
      nil)))

(defn ->dup-optimized-stack [stack]
  "Create a dup-optimized stack from a non-optimized one"
  (reduce push [] stack))

(defn- frame->vec [frame]
  (if (= 1 (:count frame)) [(:value frame)]
      (repeat (:count frame) (:value frame))))

(defn <-dup-optimized-stack [dup-optimized-stack]
  "Convert a dup-optimized stack to a plain, non-optimized version"
  (mapcat frame->vec dup-optimized-stack))
