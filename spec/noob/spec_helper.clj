(ns noob.spec-helper
  (:require [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]
            [noob.bot :as bot]
            [speclj.core :refer :all]))

(defn stub-discord []
  (around [it]
    (with-redefs [discord-ws/status-update!      (stub :discord/status-update!)
                  discord-rest/create-message!   (stub :discord/create-message!)
                  discord-rest/get-current-user! (stub :discord/get-current-user!)]
      (it))))

(defn stub-bot []
  (around [it]
    (with-redefs [bot/gateway         (constantly :bot/gateway)
                  bot/events          (constantly :bot/events)
                  bot/rest-connection (constantly :bot/rest)
                  bot/init!           (stub :bot/init!)
                  bot/stop!           (stub :bot/stop!)]
      (it))))
