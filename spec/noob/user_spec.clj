(ns noob.user-spec
  (:require [c3kit.bucket.db :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick ted]]
            [noob.user :as sut]
            [speclj.core :refer :all]))

(describe "User"
  (with-stubs)
  (bogus/with-kinds :all)

  (it "level"
    (should= 1 (sut/level {}))
    (should= 1 (sut/level {:xp -1}))
    (should= 1 (sut/level {:xp 0}))
    (should= 1 (sut/level {:xp 99}))
    (should= 2 (sut/level {:xp 100}))
    (should= 2 (sut/level {:xp 249}))
    (should= 3 (sut/level {:xp 250}))
    (should= 3 (sut/level {:xp 449}))
    (should= 4 (sut/level {:xp 450}))
    (should= 4 (sut/level {:xp 699}))
    (should= 5 (sut/level {:xp 700}))
    (should= 5 (sut/level {:xp 999}))
    (should= 6 (sut/level {:xp 1000}))
    (should= 6 (sut/level {:xp 1349}))
    (should= 7 (sut/level {:xp 1350}))
    (should= 7 (sut/level {:xp 1749}))
    (should= 8 (sut/level {:xp 1750}))
    (should= 8 (sut/level {:xp 2199}))
    (should= 9 (sut/level {:xp 2200}))
    (should= 9 (sut/level {:xp 2699}))
    (should= 10 (sut/level {:xp 2700}))
    (should= 10 (sut/level {:xp 100000})))

  (it "ability roll"
    (with-redefs [rand (constantly 0.5)]
      (should= 1.5 (sut/ability-roll 1 0))
      (should= 3.015 (sut/ability-roll 1 1) 0.001)
      (should= 3 (sut/ability-roll 2 0) 0.001)
      (should= 10.5 (sut/ability-roll 7 0) 0.001)
      (should= 12 (sut/ability-roll 8 0) 0.001)
      (should= 16.65 (sut/ability-roll 1 10))
      (should= 10.65 (sut/ability-roll 2 5) 0.001)))

  (it "gain-xp"
    (should= {:xp 25} (sut/gain-xp {} 25 1))
    (should= {:xp 27} (sut/gain-xp {} 25 2))
    (should= {:xp 122} (sut/gain-xp {:xp 100} 25 1))
    (should= {:xp 467} (sut/gain-xp {:xp 450} 25 1))
    (should= {:xp 715} (sut/gain-xp {:xp 700} 25 1))
    (should= {:xp 1012} (sut/gain-xp {:xp 1000} 25 1))
    (should= {:xp 127} (sut/gain-xp {:xp 100} 25 3))
    (should= {:xp 145} (sut/gain-xp {:xp 100} 25 10))
    (should= {:xp 2702} (sut/gain-xp {:xp 2700} 25 1)))

  (context "roll"

    (redefs-around [sut/ability-roll (stub :ability-roll)])

    (it "no equipment"
      (sut/roll @bill :attack)
      (should-have-invoked :ability-roll {:with [2 0]}))

    (it "attack with stick"
      (db/tx @bill :loadout #{(:id @stick)})
      (sut/roll @bill :attack)
      (should-have-invoked :ability-roll {:with [2 1]}))

    (it "perception with hat"
      (db/tx @ted :loadout #{(:id @propeller-hat)})
      (sut/roll @ted :perception)
      (should-have-invoked :ability-roll {:with [1 2]}))

    (it "multiple items containing different attributes"
      (db/tx @ted :loadout #{(:id @propeller-hat) (:id @stick)})
      (sut/roll @ted :perception)
      (should-have-invoked :ability-roll {:with [1 2]}))

    (it "multiple items containing the same attribute"
      (db/tx @stick :perception 5)
      (db/tx @ted :loadout #{(:id @propeller-hat) (:id @stick)})
      (sut/roll @ted :perception)
      (should-have-invoked :ability-roll {:with [1 7]}))
    )

  )
