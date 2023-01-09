(ns noob.events.ready
  (:require [discljord.connections :as discord-ws]
            [discord.api :as discord-api]
            [noob.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.slash.core :as slash]))

(defn register-global-command [args] (apply discord-api/create-global-slash-command! args))

(defn register-dev-command [args]
  (apply (partial discord-api/create-guild-slash-command! config/dev-guild) args))

(defmethod events/handle-event :ready [_ _]
  (discord-ws/status-update! (bot/gateway) :activity (discord-ws/create-activity :name "Say hello!"))
  (run! register-global-command slash/global-commands)
  (run! register-dev-command slash/dev-commands))
