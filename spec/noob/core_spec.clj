(ns noob.core-spec
  (:require [noob.core :as sut]
            [speclj.core :refer :all]))

(describe "Noob Core"

  (it "->hash-map"
    (should= {} (sut/->hash-map :a :b []))
    (should= {1 1 2 2 3 3} (sut/->hash-map identity identity [1 2 3]))
    (should= {nil nil} (sut/->hash-map :a :b [nil]))
    (should= {1 2 3 4} (sut/->hash-map :a :b [{:a 1 :b 2} {:a 3 :b 4 :c 5}])))

  (it "**"
    (should= 1 (sut/** 0 0))
    (should= 1 (sut/** -1 0))
    (should= 1 (sut/** 1 0))
    (should= 2 (sut/** 2 1))
    (should= 4 (sut/** 2 2))
    (should= 9 (sut/** 3 2))
    (should= 1/2 (sut/** 2 -1))
    (should= 1/4 (sut/** 2 -2)))

  )