(ns noob.slash.action.inventory-button
  (:require [c3kit.apron.schema :as schema]
            [discord.interaction :as interaction]
            [noob.product :as product]
            [noob.slash.command.inventory :as inventory]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(defn ->product [request]
  (->> request :data :custom-id (re-find #"\d+") schema/->int product/entity))

(defn toggle-item! [toggle! request user product]
  (let [user (toggle! user product)
        [content options] (inventory/->inventory-content request user)]
    (interaction/update-message! request content options)))

(defmethod slash/handle-action "inventory-button" [request]
  (let [user    (user/current request)
        product (->product request)]
    (cond
      (nil? product)
      (interaction/reply-ephemeral! request "This item does not exist.")

      (user/equipped? user product)
      (toggle-item! user/unequip! request user product)

      (user/owns? user product)
      (toggle-item! user/equip! request user product)

      :else (interaction/reply-ephemeral! request "You do not own this item."))))
