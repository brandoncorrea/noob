(ns discord.bot
  (:require [c3kit.apron.app :as app]
            [c3kit.apron.log :as log]
            [clojure.core.async :as async]
            [discljord.connections :as discord-ws]
            [discljord.events :as discord-events]
            [discljord.messaging :as discord-rest]
            [discord.thread :as thread]))

(defonce config (atom nil))
(defn configure! [& {:as conf}] (reset! config conf))

(def state (app/resolution :discord/bot))

(defn rest-connection [] (:rest @state))
(defn gateway [] (:gateway @state))
(defn events [] (:events @state))

(defn fetch-id [rest]
  (-> rest discord-rest/get-current-user! deref :id))

(defn ->message-task [event-handler chan]
  (when event-handler
    (let [handler #(discord-events/message-pump! chan event-handler)
          thread  (thread/->Thread handler)]
      (thread/start thread)
      thread)))

(defn create-bot! [{:keys [token intents event-handler]}]
  (let [event-channel (async/chan 100)
        rest          (discord-rest/start-connection! token)
        bot-id        (fetch-id rest)]
    (log/info "Bot ID:" bot-id)
    {:events         event-channel
     :gateway        (discord-ws/connect-bot! token event-channel :intents (set intents))
     :rest           rest
     :message-thread (->message-task event-handler event-channel)
     :id             bot-id}))

(defn start-service [app]
  (log/info "Starting Discord Bot Service")
  (assoc app :discord/bot (create-bot! @config)))

(defn stop-service [app]
  (log/info "Stopping Discord Bot Service")
  (let [{:keys [rest gateway events message-thread]} (:discord/bot app)]
    (some-> message-thread thread/interrupt)
    (some-> rest discord-rest/stop-connection!)
    (some-> gateway discord-ws/disconnect-bot!)
    (some-> events async/close!)
    (dissoc app :discord/bot)))

(def service (app/service 'discord.bot/start-service 'discord.bot/stop-service))
