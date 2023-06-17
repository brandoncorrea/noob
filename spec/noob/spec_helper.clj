(ns noob.spec-helper
  (:require [c3kit.apron.time :as time]
            [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]
            [discord.interaction :as interaction]
            [noob.bot :as bot]
            [speclj.core :refer :all]))

(defn stub-discord []
  (redefs-around [discord-ws/status-update! (stub :discord/status-update!)
                  interaction/reply! (stub :discord/reply-interaction!)
                  discord-rest/create-message! (stub :discord/create-message!)
                  discord-rest/get-current-user! (stub :discord/get-current-user!)]))

(defn stub-bot []
  (redefs-around [bot/gateway (constantly :bot/gateway)
                  bot/events (constantly :bot/events)
                  bot/rest-connection (constantly :bot/rest)
                  bot/init! (stub :bot/init!)
                  bot/stop! (stub :bot/stop!)]))

(defn stub-now [time]
  (redefs-around [time/now (stub :now {:return time})]))
