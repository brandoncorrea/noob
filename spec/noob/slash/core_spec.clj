(ns noob.slash.core-spec
  (:require [noob.slash.core :as sut]
            [speclj.core :refer :all]))

(describe "Slash Core"

  (it "normalizes options"
    (should-be-nil (sut/normalize-options nil))
    (should= {} (sut/normalize-options {}))
    (should= {:a :b} (sut/normalize-options {:a :b}))
    (should= {:a :b :data {:options nil :c :d}} (sut/normalize-options {:a :b :data {:options nil :c :d}}))
    (should= {:a :b :data {:options {} :c :d}} (sut/normalize-options {:a :b :data {:options [] :c :d}}))
    (should= {:data {:options {"hello" 1 "world" "blah"}}}
             (sut/normalize-options {:data {:options [{:name "hello" :value 1 :foo :bar}
                                                      {:name "world" :value "blah" :bar :baz}]}})))
  )
