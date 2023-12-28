(ns discord.interaction
  (:require [c3kit.apron.corec :as ccc]
            [discord.api :as api]
            [discord.components.core :as components]
            [medley.core :as medley]
            [noob.core :as core]))

(def flags
  {
   :cross-posted            (core/** 2 0)
   :cross-post              (core/** 2 1)
   :suppress-embeds         (core/** 2 2)
   :source-message-deleted  (core/** 2 3)
   :urgent                  (core/** 2 4)
   :has-thread              (core/** 2 5)
   :ephemeral               (core/** 2 6)
   :loading                 (core/** 2 7)
   :failed-to-mention-roles (core/** 2 8)
   :suppress-notifications  (core/** 2 12)
   :voice-message           (core/** 2 13)
   })

(defn ->flag [fs] (ccc/sum-by flags fs))

(defn- content->data [content]
  (if (string? content)
    {:content content :components []}
    {:components [{:type 1 :components (components/hiccup->components content)}]}))

(defn- ->message-data [content {:keys [flags embed]}]
  (cond-> (some-> content content->data)
          (seq flags) (assoc :flags (->flag flags))
          embed (assoc :embeds [embed])))

(defn- ->message-body [type content options]
  (medley/assoc-some {:type type} :data (->message-data content options)))

(defn- callback! [type {:keys [id token]} content options]
  (when (and id token)
    (api/post! (str "/interactions/" id "/" token "/callback")
               (->message-body type content options))))

(defn reply!
  "Reply to the interaction.
   Acknowledges the interaction."
  [request content & {:as options}]
  (callback! 4 request content options))

(defn reply-ephemeral!
  "Reply to the interaction with the :ephemeral flag.
   Acknowledges the interaction."
  [payload content] (reply! payload content :flags [:ephemeral]))

(defn update-message!
  "Update the original message.
   Acknowledges the interaction."
  [request content & {:as options}]
  (callback! 7 request content options))

(defn edit-original!
  "Edit the original message.
   Does not acknowledge the interaction."
  [request content & {:as options}]
  (let [{:keys [id channel-id]} (:message request)]
    (when (and id channel-id content)
      (api/patch! (str "/channels/" channel-id "/messages/" id)
                  (->message-data content options)))))

(defn embed! [request embed] (reply! request nil :embed embed))

(defn create-message! [request content]
  (let [channel-id (:channel-id request)]
    (when (and channel-id content)
      (api/post! (str "/channels/" channel-id "/messages") (content->data content)))))
