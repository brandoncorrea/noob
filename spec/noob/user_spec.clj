(ns noob.user-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick ted]]
            [noob.roll :as roll]
            [noob.user :as sut]
            [speclj.core :refer :all]))

(describe "User"
  (with-stubs)
  (bogus/with-kinds :all)

  (it "avatar"
    (let [avatar "https://cdn.discordapp.com/avatars/user-id/cat.png"]
      (should= avatar (sut/avatar {:id "user-id" :avatar "cat"}))
      (should= avatar (sut/avatar {:user {:id "user-id" :avatar "cat"}}))
      (should= avatar (sut/avatar {:user {:id "user-id"} :avatar "cat"}))
      (should= avatar (sut/avatar {:user {:id "user-id"} :avatar "cat"}))
      (should-be-nil (sut/avatar {}))))

  (it "display-name"
    (let [name "billy"]
      (should= name (sut/display-name {:nick "billy"}))
      (should= name (sut/display-name {:global-name "billy"}))
      (should= name (sut/display-name {:user {:global-name "billy"}}))
      (should-be-nil (sut/display-name {}))))

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

    (redefs-around [roll/ability (stub :ability-roll)])

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
