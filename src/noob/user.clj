(ns noob.user
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [noob.core :as core]
            [noob.product :as product]
            [noob.roll :as roll]))

(defn by-discord-id [id] (db/ffind-by :user :discord-id id))
(defn discord-id [request] (-> request :member :user :id))
(defn current [request] (-> request discord-id by-discord-id))
(defn mention [user] (str "<@" (:discord-id user user) \>))
(defn create [discord-id] {:kind :user :discord-id discord-id})
(defn create! [discord-id] (db/tx (create discord-id)))
(defn find-or-create [discord-id]
  (or (by-discord-id discord-id)
      (create discord-id)))

(defn inventory [user] (-> user :inventory utilc/<-edn))
(defn inventory! [user] (-> user inventory product/entities))
(defn loadout [user] (-> user :loadout utilc/<-edn))
(defn loadout! [user] (-> user loadout product/entities))

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
(defn owns? [user item] (some (partial = (:id item item)) (inventory user)))
(defn unslotted? [user slot]
  (not-any? #(-> % product/entity :slot (= slot)) (loadout user)))

(defn equipped? [user item]
  (core/some= (:id item item) (loadout user)))

(defn swap-slotted-item [user item]
  (->> (loadout! user)
       (remove #(= (:slot %) (:slot item)))
       (map :id)
       (cons (:id item))
       utilc/->edn))

(defn equip [user item]
  (assoc user :loadout (swap-slotted-item user item)))

(defn equip! [user item] (db/tx (equip user item)))

(defn unequip [user item]
  (assoc user :loadout (utilc/->edn (ccc/removev= (loadout user) (:id item item)))))

(defn unequip! [user item] (db/tx (unequip user item)))

(defn ability-score [user ability]
  (->> (loadout! user)
       (ccc/map-some ability)
       (reduce +)))

(defn roll [user ability]
  (roll/ability (level user) (ability-score user ability)))

(defn gain-xp [user base-factor challenge-level]
  (let [reward (roll/xp-reward (level user) base-factor challenge-level)
        xp     (+ (:xp user 0) reward)]
    (assoc user :xp xp)))

(defn gain-xp! [user base-factor challenge-level]
  (db/tx (gain-xp user base-factor challenge-level)))

(defn loot [user item]
  (assoc user :inventory (utilc/->edn (conj (inventory user) (:id item)))))

(defn loot! [user item] (db/tx (loot user item)))

(defn purchase! [user item]
  (-> user
      (loot item)
      (withdraw-niblets (:price item))
      db/tx))

(defn display-name [e-or-request]
  (let [{:keys [nick global-name user]} (:member e-or-request e-or-request)]
    (or nick global-name (:global-name user))))

(defn resolved-name [request discord-id]
  (let [discord-id (:discord-id discord-id discord-id)
        {:keys [users members]} (-> request :data :resolved)
        member     (get members discord-id)
        user       (get users discord-id)]
    (or (:nick member)
        (:global-name user)
        (:username user))))

(defn avatar [member-or-user]
  (let [user-id (or (:id member-or-user)
                    (-> member-or-user :user :id))
        avatar  (or (-> member-or-user :avatar)
                    (-> member-or-user :user :avatar))]
    (when (and user-id avatar)
      (str "https://cdn.discordapp.com/avatars/" user-id "/" avatar ".png"))))

(defn ->author [member-or-user]
  {:name     (display-name member-or-user)
   :icon_url (avatar member-or-user)})
