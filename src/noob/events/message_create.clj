(ns noob.events.message-create
  (:require [discljord.formatting :as formatting]
            [discljord.messaging :as discord-rest]
            [noob.bot :as bot]
            [noob.events.core :as events]))

(def responses ["Hello there" "Good evening" "Good morning" "G'day" "Hi" "Howdy :cowboy:"])

(defn random-response [user]
  (str (rand-nth responses) ", " (formatting/mention-user user) \!))

(defmethod events/handle-event :message-create
  [_ {:keys [channel-id author mentions] :as data}]
  (when (some #{@bot/id} (map :id mentions))
    (discord-rest/create-message! (bot/rest-connection) channel-id :content (random-response author))))
