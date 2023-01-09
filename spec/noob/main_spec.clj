(ns noob.main-spec
  (:require [discljord.events :as discord-events]
            [noob.events :as events]
            [noob.main :as sut]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]
            [speclj.stub :as stub]))

(describe "Main"
  (with-stubs)
  (spec-helper/stub-bot)

  (around [it]
    (with-redefs [discord-events/message-pump! (stub :discord/message-pump!)]
      (it)))

  (it "initializes with bot token"
    (with-redefs [slurp (fn [file] ({"config.edn" "{:token \"TOKEN\"}"} file))]
      (sut/-main)
      (let [[init! pump! stop!] @stub/*stubbed-invocations*]
        (should= [:bot/init! ["TOKEN"]] init!)
        (should= [:discord/message-pump! [:bot/events events/handle-event]] pump!)
        (should= [:bot/stop! []] stop!))))
  )
