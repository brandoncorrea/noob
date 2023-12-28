(ns noob.slash.action.inventory-button
  (:require [c3kit.apron.schema :as schema]
            [discord.interaction :as interaction]
            [noob.product :as product]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(defn ->product [request]
  (->> request :data :custom-id (re-find #"\d+") schema/->int product/entity))

(defmethod slash/handle-action "inventory-button" [request]
  (let [user    (user/current request)
        product (->product request)]
    (cond
      (nil? product)
      (interaction/reply-ephemeral! request "This item does not exist.")

      ;; TODO [BAC]: Update original message with new buttons rather than sending an ephemeral message
      (user/equipped? user product)
      (do (user/unequip! user product)
          (interaction/reply-ephemeral! request "Item unequipped!"))

      ;; TODO [BAC]: Update original message with new buttons rather than sending an ephemeral message
      (user/owns? user product)
      (do (user/equip! user (product/entity product))
          (interaction/reply-ephemeral! request "Item equipped!"))

      :else (interaction/reply-ephemeral! request "You do not own this item."))))
