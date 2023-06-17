(ns discord.interaction
  (:require [discord.api :as api]
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

(defn ->flag [fs]
  (transduce (map flags) + fs))

(defn reply! [{:keys [id token]} content & fs]
  (when (and id token content)
    (api/post! (str "/interactions/" id "/" token "/callback")
               (cond-> {:type 4 :data {:content content}}
                       (seq fs)
                       (assoc-in [:data :flags] (->flag fs))))))

(defn reply-ephemeral! [payload content]
  (reply! payload content :ephemeral))
