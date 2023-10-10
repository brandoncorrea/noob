(ns noob.main
  (:require [c3kit.apron.app :as app]
            [c3kit.apron.legend :as legend]
            [c3kit.apron.log :as log]
            [c3kit.bucket.db :as db]
            [discljord.events :as discord-events]
            [noob.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.message-create]
            [noob.events.ready]
            [noob.schema.full :as schema.full]
            [noob.slash.give]
            [noob.slash.recurrent]
            [noob.slash.shop]))

(defn start-db [] (app/start! [db/service]))

(defn wrap-error [f]
  (fn [& args]
    (try
      (apply f args)
      (catch Exception e
        (log/error e)))))

(defn -main [& _]
  (try
    (legend/init! schema.full/legend)
    (start-db)
    (bot/init! config/token)
    (discord-events/message-pump! (bot/events) (wrap-error events/handle-event))
    (finally (bot/stop!))))
