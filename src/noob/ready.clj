(ns noob.ready
  (:require [discljord.connections :as discord-ws]
            [noob.events :as events]
            [noob.bot :as bot]))

(defmethod events/handle-event :ready
  [_ _]
  (discord-ws/status-update! (bot/gateway) :activity (discord-ws/create-activity :name "Say hello!")))
