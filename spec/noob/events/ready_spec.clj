(ns noob.events.ready-spec
  (:require [discljord.connections :as discord-ws]
            [discord.api :as discord-api]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.ready]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Ready Event"
  (with-stubs)
  (spec-helper/stub-discord)
  (spec-helper/stub-bot)

  (redefs-around [discord-api/create-global-slash-command! (stub :create-global-command!)
                  discord-api/create-guild-slash-command!  (stub :create-guild-command!)
                  discord-ws/create-activity               hash-map
                  config/dev-guild                         456
                  slash/dev-commands                       [["shop" "Get in, loser. We're going shopping!"]]
                  discord-api/get-global-commands          (constantly [{:name "global-command" :id 123 :type 1}])
                  discord-api/get-guild-commands           {456 [{:name "guild-command" :id 789 :type 1}]}
                  discord-api/delete-guild-slash-command!  (stub :delete-guild-slash-command!)
                  discord-api/delete-global-slash-command! (stub :delete-global-slash-command!)])

  (it "status says hello!"
    (events/handle-event :ready nil)
    (should-have-invoked :discord/status-update! {:with [:bot/gateway :activity {:name "Say hello!"}]}))

  (it "synchronizes dev slash commands"
    (events/handle-event :ready nil)
    (should-have-invoked :delete-guild-slash-command! {:with [456 789]})
    (should-have-invoked :create-guild-command! {:with [456 "shop" "Get in, loser. We're going shopping!" nil]}))

  (context "global synchronization"

    (it "synchronizes slash commands"
      (with-redefs [discord-api/get-global-commands (constantly [{:name "global-command" :id 123 :type 1}])]
        (events/handle-event :ready nil)
        (should-have-invoked :delete-global-slash-command! {:with [123]})
        (should-have-invoked :create-global-command! {:with ["daily" "Redeem your daily Niblets!" nil]})))

    (it "command already exists"
      (with-redefs [discord-api/get-global-commands (constantly
                                                      (mapv (fn [[name description options]]
                                                              {:id          123
                                                               :name        name
                                                               :type        1
                                                               :description description
                                                               :options     options})
                                                            slash/global-commands))]
        (events/handle-event :ready nil)
        (should-not-have-invoked :delete-global-slash-command!)
        (should-not-have-invoked :create-global-command!)))

    (it "command exists, but not as a slash command"
      (with-redefs [discord-api/get-global-commands (constantly
                                                      (mapv (fn [[name description options]]
                                                              {:id          123
                                                               :type        2
                                                               :name        name
                                                               :description description
                                                               :options     options})
                                                            slash/global-commands))]
        (events/handle-event :ready nil)
        (should-have-invoked :delete-global-slash-command!)
        (should-have-invoked :create-global-command!)))
    )
  )
