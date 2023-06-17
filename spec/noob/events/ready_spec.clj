(ns noob.events.ready-spec
  (:require [discljord.connections :as discord-ws]
            [discord.api :as discord-api]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Ready Event"
  (with-stubs)
  (spec-helper/stub-discord)
  (spec-helper/stub-bot)
  (redefs-around [discord-api/create-global-slash-command! (stub :create-global-command!)
                  discord-api/create-guild-slash-command! (stub :create-guild-command!)])

  (it "status says hello!"
    (with-redefs [discord-ws/create-activity (fn [& {:as args}] args)]
      (events/handle-event :ready nil)
      (should-have-invoked :discord/status-update! {:with [:bot/gateway :activity {:name "Say hello!"}]})))

  (it "registers dev slash commands"
    (events/handle-event :ready nil)
    (should-have-invoked :create-guild-command! {:with [config/dev-guild  "daily" "Redeem your daily Niblets!"]}))

  (it "registers global slash commands"
    (events/handle-event :ready nil)
    (should-not-have-invoked :create-global-command!))
  )
