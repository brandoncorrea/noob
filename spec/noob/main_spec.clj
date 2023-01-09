(ns noob.main-spec
  (:require [discljord.events :as discord-events]
            [noob.config :as config]
            [noob.events.core :as events]
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
    (sut/-main)
    (let [[init! pump! stop!] @stub/*stubbed-invocations*]
      (should= [:bot/init! [config/token]] init!)
      (should= [:discord/message-pump! [:bot/events events/handle-event]] pump!)
      (should= [:bot/stop! []] stop!)))
  )
