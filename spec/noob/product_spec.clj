(ns noob.product-spec
  (:require [noob.bogus :as bogus]
            [noob.product :as sut]
            [speclj.core :refer :all]))

(describe "Product"
  (with-stubs)
  (bogus/with-kinds :all)

  (it "names"
    (should-be-nil (sut/slot-names :foo))
    (should= "Back" (sut/slot-names :back))
    (should= "Chest" (sut/slot-names :chest))
    (should= "Feet" (sut/slot-names :feet))
    (should= "Hands" (sut/slot-names :hands))
    (should= "Head" (sut/slot-names :head))
    (should= "Legs" (sut/slot-names :legs))
    (should= "Main Hand" (sut/slot-names :main-hand))
    (should= "Off-Hand" (sut/slot-names :off-hand)))

  )
