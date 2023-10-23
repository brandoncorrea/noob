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

(defn deposit-niblets [user amount] (update-niblets + user amount))
(defn withdraw-niblets [user amount] (update-niblets - user amount))

(defn transfer-niblets! [from to amount]
  (db/tx* [(withdraw-niblets from amount)
           (deposit-niblets to amount)]))

(defn level [user]
  (condp > (:xp user 0)
    100 1
    250 2
    450 3
    700 4
    1000 5
    1350 6
    1750 7
    2200 8
    2700 9
    10))

(defn niblets [user] (:niblets user 0))
(defn owns? [user item] (contains? (:inventory user) (:id item)))
(defn unslotted? [user slot]
  (not-any? #(-> % db/entity :slot (= slot)) (:loadout user)))

(defn equip [user item] (update user :loadout conj (:id item)))

(defn purchase! [user item]
  (-> user
      (update :inventory conj (:id item))
      (withdraw-niblets (:price item))
      db/tx))

(def find-or-create (some-fn by-discord-id create))
