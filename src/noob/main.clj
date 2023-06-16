(ns noob.main
  (:require [c3kit.apron.app :as app]
            [c3kit.apron.legend :as legend]
            [c3kit.bucket.db :as db]
            [discljord.events :as discord-events]
            [noob.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.message-create]
            [noob.events.ready]
            [noob.schema.full :as schema.full]
            [noob.slash.recurrent]))

(def start-db #(app/start! [db/service]))

(defn -main [& _]
  (try
    (legend/init! schema.full/legend)
    (start-db)
    (bot/init! config/token)
    (discord-events/message-pump! (bot/events) events/handle-event)
    (finally (bot/stop!))))
