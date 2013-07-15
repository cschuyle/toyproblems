(ns spiral.core)

(def blank "-")
(defn- initial-spiral []
  {
   :arr [blank blank blank blank blank
         blank blank blank blank blank
         blank blank 1     blank blank
         blank blank blank blank blank
         blank blank blank blank blank]
   :dir \N
   :row 3
   :col 3
   } )

(defn dimension [arr]
  (int (Math/sqrt (.size (arr :arr))) ))

(defn- index-for [spiral row col]
  (let [row-offset (* (dimension spiral) (- row 1)), col-offset (- col 1) ]
      (+ row-offset col-offset)))

(defn- index-for-coordinates [spiral coordinates]
  (index-for spiral (coordinates :row) (coordinates :col)))

(defn- get-cell [spiral row col]
  ((spiral :arr) (index-for spiral row col)))

(defn- print-row [spiral row col]
  (when (pos? col)
    (clojure.core/print (get-cell spiral row (- (+ 1 (dimension spiral)) col)))
    (clojure.core/print " ")
    (if (= 1 col)
      (clojure.core/print \newline))
    (recur spiral row (- col 1))))

(defn- print-spiral [spiral row]
  (when (pos? row)
    (print-row spiral (- (+ 1 (dimension spiral)) row) (dimension spiral))
    (recur spiral (- row 1))))

(defn- coordinates-after-moving [arr direction]
  (case direction
    \N { :row (- (arr :row) 1), :col (arr :col) }
    \S { :row (+ (arr :row) 1), :col (arr :col) }
    \E { :row (arr :row)      , :col (+ (arr :col) 1) }
    \W { :row (arr :row)      , :col (- (arr :col) 1) } ))

(defn- rotate-left [direction]
  (case direction
    \N \W
    \S \E
    \E \N
    \W \S))

(defn rotate-right [direction]
  (rotate-left (rotate-left (rotate-left direction))))

(defn- index-after-moving [arr direction]
  (index-for-coordinates arr (coordinates-after-moving arr direction)))

(defn- forward-cell [arr]
  (index-after-moving arr (arr :dir)))

(defn- left-cell [arr]
  (index-after-moving arr (rotate-left (arr :dir))))

(defn- right-cell [arr]
  (index-after-moving arr (rotate-right (arr :dir))))

(defn- is-blank [arr index]
  (= blank ((arr :arr) index)))

(defn- should-turn? [arr]
    (and (is-blank arr (forward-cell arr))
       (is-blank arr (left-cell arr))
       (is-blank arr (right-cell arr))))

(defn move-forward [arr direction]
  (coordinates-after-moving arr direction))

(defn next-generation [arr]
  (let [new-direction (if (should-turn? arr) 
                        (rotate-right (arr :dir))
                        (arr :dir))
        target-coordinates (move-forward arr new-direction) 
        new-value          (+ 1 ((arr :arr) (index-for arr (arr :row) (arr :col))))
        constructor        (fn [index existing-value] 
                             (if (= index (index-for-coordinates arr target-coordinates))
                               new-value existing-value))] 
    {
     :arr (vec (map-indexed constructor (arr :arr)))
     :dir new-direction 
     :row (target-coordinates :row) 
     :col (target-coordinates :col) 
     }))

(defn spiral-of [goal]
  (loop [spiral (initial-spiral), up-to 1] 
    (cond 
     (= up-to goal) spiral 
     (< up-to goal) (recur (next-generation spiral) (+ 1 up-to)))))

(defn display [spiral]
  (print-spiral spiral (dimension spiral)))

 (display (spiral-of 13))

nil
