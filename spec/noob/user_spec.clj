(ns noob.user-spec
  (:require [noob.user :as sut]
            [speclj.core :refer :all]))

(describe "User"

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

  )
