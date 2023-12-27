(ns noob.slash.action.shop-menu
  (:require [c3kit.apron.schema :as schema]
            [c3kit.bucket.api :as db]
            [discord.interaction :as interaction]
            [noob.slash.command.shop :as shop]
            [noob.slash.core :as slash]
            [noob.user :as user]))

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
    (interaction/edit-original! request (shop/->shop-response user))
    (interaction/reply-ephemeral! request (str (:name item) " has been added to your inventory!"))))

(defn requested-product [request]
  (->> request :data :values first schema/->int (db/entity :product)))

(defmethod slash/handle-action "shop-menu" [request]
  (let [{:keys [price level name] :as item} (requested-product request)
        user (delay (-> request user/discord-id user/find-or-create))]
    (cond
      (nil? item) (interaction/reply-ephemeral! request "That item doesn't seem to exist.")
      (user/owns? @user item) (interaction/reply-ephemeral! request (str "It looks like you already own a " name "."))
      (> price (user/niblets @user)) (interaction/reply-ephemeral! request "You do not have enough Niblets to purchase this item.")
      (> level (user/level @user)) (interaction/reply-ephemeral! request (str "You need to be at least level " level " to purchase this item."))
      :else (purchase! request @user item))))
