(ns noob.slash.shop
  (:require [c3kit.apron.schema :as schema]
            [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [discord.interaction :as interaction]
            [noob.product :as product]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(defn non-zero? [n] (and n (not (zero? n))))
(defn ->descriptor [{:keys [slot name price attack defense sneak perception]}]
  (cond-> (str name " – " (product/slot-names slot) " – 🪙 " price)
          (non-zero? attack) (str " ⚔️ " attack)
          (non-zero? perception) (str " 👁 " perception)
          (non-zero? sneak) (str " 🥷 " sneak)
          (non-zero? defense) (str " 🛡 " defense)))

(defn ->option [{:keys [description id] :as product}]
  [:option {:description description :value id} (->descriptor product)])

(def menu-root [:select#shop-menu {:placeholder "Select an option"}])
(defn ->shop-menu [products]
  (into menu-root (map ->option) products))

(defn ->shop-response [user]
  (let [owner? (comp (-> user user/inventory set) :id)]
    (if-let [products (->> (db/find-by :product :level ['<= (user/level user)])
                           (remove owner?)
                           seq)]
      (->shop-menu products)
      "There are no items available to purchase.")))

(defmethod slash/handle-name "shop" [request]
  (interaction/reply! request (->shop-response (user/current request))))

(defn maybe-equip [user item]
  (cond-> user
          (user/unslotted? user (:slot item))
          (user/equip item)))

(defn purchase-and-equip! [user item]
  (-> user
      (maybe-equip item)
      (user/purchase! item)))

(defn purchase! [request user item]
  (let [user (purchase-and-equip! user item)]
    (interaction/edit-original! request (->shop-response user))
    (interaction/reply-ephemeral! request (str (:name item) " has been added to your inventory!"))))

(defmethod slash/handle-custom-id "shop-menu" [request]
  (let [item (->> request :data :values first schema/->int (db/entity :product))
        user (delay (-> request user/discord-id user/find-or-create))]
    (cond
      (nil? item) (interaction/reply-ephemeral! request "That item doesn't seem to exist.")
      (user/owns? @user item) (interaction/reply-ephemeral! request (str "It looks like you already own a " (:name item) "."))
      (> (:price item) (user/niblets @user)) (interaction/reply-ephemeral! request "You do not have enough Niblets to purchase this item.")
      (> (:level item) (user/level @user)) (interaction/reply-ephemeral! request (str "You need to be at least level " (:level item) " to purchase this item."))
      :else (purchase! request @user item))))
