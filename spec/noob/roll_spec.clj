(ns noob.roll-spec
  (:require [speclj.core :refer :all]
            [noob.roll :as sut]))

(describe "Roll"

  (it "ability roll"
    (with-redefs [rand (constantly 0.5)]
      (should= 1.5 (sut/ability 1 0))
      (should= 3.015 (sut/ability 1 1) 0.001)
      (should= 3 (sut/ability 2 0) 0.001)
      (should= 10.5 (sut/ability 7 0) 0.001)
      (should= 12 (sut/ability 8 0) 0.001)
      (should= 16.65 (sut/ability 1 10))
      (should= 10.65 (sut/ability 2 5) 0.001)))

  )