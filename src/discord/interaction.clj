(ns discord.interaction
  (:require [c3kit.apron.corec :as ccc]
            [discord.api :as api]
            [discord.components.core :as components]
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

(defn- ->data [content]
  (if (string? content)
    {:content content :components []}
    {:components [{:type 1 :components (components/hiccup->components content)}]}))

(defn reply! [{:keys [id token]} content & {:keys [flags embed]}]
  (when (and id token)
    (api/post! (str "/interactions/" id "/" token "/callback")
               (cond-> {:type 4}
                       content (assoc :data (->data content))
                       (seq flags) (assoc-in [:data :flags] (->flag flags))
                       embed (assoc-in [:data :embeds] [embed])))))

(defn embed! [request embed] (reply! request nil :embed embed))
(defn reply-ephemeral! [payload content] (reply! payload content :flags [:ephemeral]))

(defn edit-original! [request content]
  (let [{:keys [id channel-id]} (:message request)]
    (when (and id channel-id content)
      (api/patch! (str "/channels/" channel-id "/messages/" id) (->data content)))))

(defn create-message! [request content]
  (let [channel-id (:channel-id request)]
    (when (and channel-id content)
      (api/post! (str "/channels/" channel-id "/messages") (->data content)))))
