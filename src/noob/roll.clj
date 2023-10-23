(ns noob.roll
  "Formulas for various rolls.")

(defn ability [level ability-score]
  (let [stat-factor   (+ 1 (/ ability-score 100))
        base-factor   (+ (* level stat-factor) ability-score)
        random-factor (+ 1 (rand))]
    (* base-factor random-factor)))

(defn xp-reward [user-level base-factor challenge-level]
  (let [level-difference (- challenge-level user-level)
        penalty          (/ level-difference 10)]
    (int (* base-factor (+ 1 penalty)))))

(defn steal? [thief-level thief-sneak victim-level victim-perception]
  (pos? (+ thief-level thief-sneak
           (- victim-level victim-perception)
           (- (rand-int 11) 5))))

(defn stolen-niblets [thief-level thief-sneak victim-level victim-perception]
  (let [level-factor  (* 2 thief-level)
        victim-factor (* -1 victim-level victim-perception)]
    (+ 3 level-factor thief-sneak victim-factor (rand-int 6))))
