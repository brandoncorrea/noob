(ns noob.events.ready
  (:require [discljord.connections :as discord-ws]
            [discord.api :as api]
            [discord.api]
            [discord.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.slash.command.schema.full :as slash-schema]))

(defn- commands= [command-1 command-2]
  (every? #(= (% command-1) (% command-2)) [:type :name :description :options]))

(defn- remove-commands-from [coll-1 coll-2]
  (remove #(some (partial commands= %) coll-1) coll-2))

;; TODO [BAC]: This could be better.
;;   Rather than recreating commands, PATCH many commands at once with its new options.
(defn- sync-commands! [commands get-commands delete-command! create-command!]
  (let [existing (map #(select-keys % [:id :name :type :description :options]) (get-commands))]
    (->> (remove-commands-from commands existing)
         (run! (comp delete-command! :id)))
    (run! create-command! (remove-commands-from existing commands))))

(defn sync-dev-commands! []
  (sync-commands!
    slash-schema/dev-commands
    #(api/get-guild-commands config/dev-guild)
    (partial api/delete-guild-slash-command! config/dev-guild)
    (partial api/create-guild-slash-command! config/dev-guild)))

(defn sync-global-commands! []
  (sync-commands!
    slash-schema/prod-commands
    api/get-global-commands
    api/delete-global-slash-command!
    api/create-global-slash-command!))

(defmethod events/handle-event :ready [_ _]
  (discord-ws/status-update! (bot/gateway) :activity (discord-ws/create-activity :name "Say hello!"))
  (sync-global-commands!)
  (sync-dev-commands!))
