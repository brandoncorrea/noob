(ns noob.user
  (:require [c3kit.bucket.db :as db]
            [discljord.formatting :as formatting]))

(defn by-discord-id [id] (db/ffind-by :user :discord-id id))
(def discord-user (comp :user :member))
(def username (comp :username discord-user))
(def discord-id (comp :id discord-user))
(def current (comp by-discord-id discord-id))
(def mention (comp formatting/mention-user :discord-id))

(defn create [discord-id]
  {:kind       :user
   :id         (db/tempid)
   :discord-id discord-id})

(def create! (comp db/tx create))

(defn update-niblets [f user amount]
  (if (:niblets user)
    (update user :niblets f amount)
    (assoc user :niblets (f amount))))

(def deposit-niblets (partial update-niblets +))
(def withdraw-niblets (partial update-niblets -))

(defn transfer-niblets! [from to amount]
  (db/tx* [(withdraw-niblets from amount)
           (deposit-niblets to amount)]))

(def find-or-create (some-fn by-discord-id create))
