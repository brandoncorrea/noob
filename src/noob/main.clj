(ns noob.main
  (:require [clojure.edn :as edn]
            [discljord.events :as discord-events]
            [noob.bot :as bot]
            [noob.events :as events]
            [noob.message-create]
            [noob.ready]))

(defn -main [& _]
  (try
    (-> "config.edn" slurp edn/read-string :token bot/init!)
    (discord-events/message-pump! (bot/events) events/handle-event)
    (finally (bot/stop!))))
