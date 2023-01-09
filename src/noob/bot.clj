(ns noob.bot
  (:require [clojure.core.async :as async]
            [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]))

(def state (atom nil))
(def id (atom nil))

(defn rest-connection [] (:rest @state))
(defn gateway [] (:gateway @state))
(defn events [] (:events @state))

(defn start! [token & intents]
  (let [event-channel (async/chan 100)]
    {:events  event-channel
     :gateway (discord-ws/connect-bot! token event-channel :intents (set intents))
     :rest    (discord-rest/start-connection! token)}))

(defn stop! []
  (let [{:keys [rest gateway events]} @state]
    (discord-rest/stop-connection! rest)
    (discord-ws/disconnect-bot! gateway)
    (async/close! events)))

(defn init! [token]
  (reset! state (start! token :guild-messages))
  (reset! id (:id @(discord-rest/get-current-user! (rest-connection)))))
