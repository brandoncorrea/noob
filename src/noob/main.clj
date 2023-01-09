(ns noob.main
  (:require [discljord.events :as discord-events]
            [noob.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.message-create]
            [noob.events.ready]
            [noob.slash.recurrent]))

(defn -main [& _]
  (try
    (bot/init! config/token)
    (discord-events/message-pump! (bot/events) events/handle-event)
    (finally (bot/stop!))))
