(ns noob.slash.core-spec
  (:require [noob.slash.core :as sut]
            [speclj.core :refer :all]))

(describe "Slash Core"

  (it "dev commands"
    (should= "Redeem your daily Niblets!" (get sut/dev-commands "daily"))
    (should= "Redeem your weekly Niblets!" (get sut/dev-commands "weekly"))
    (should= 2 (count sut/dev-commands)))

  (it "global commands"
    (should= 0 (count sut/global-commands)))

  )