(ns noob.events.ready
  (:require [discljord.connections :as discord-ws]
            [noob.bot :as bot]
            [noob.events.core :as events]))

(defmethod events/handle-event :ready
  [_ _]
  (discord-ws/status-update! (bot/gateway) :activity (discord-ws/create-activity :name "Say hello!")))
