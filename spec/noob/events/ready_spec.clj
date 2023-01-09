(ns noob.events.ready-spec
  (:require [discljord.connections :as discord-ws]
            [noob.events.core :as events]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Ready Event"
  (with-stubs)
  (spec-helper/stub-discord)
  (spec-helper/stub-bot)

  (it "status says hello!"
    (with-redefs [discord-ws/create-activity (fn [& {:as args}] args)]
      (events/handle-event :ready nil)
      (should-have-invoked :discord/status-update! {:with [:bot/gateway :activity {:name "Say hello!"}]})))
  )
