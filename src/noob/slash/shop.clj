(ns noob.slash.shop
  (:require [c3kit.bucket.db :as db]
            [discord.interaction :as interaction]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(defn ->option [{:keys [description id name]}]
  [:option {:description description :value id} name])

(def menu-root [:select#shop-menu {:placeholder "Select an option"}])
(defn ->select-menu [products]
  (into menu-root (map ->option) products))

(defn ->shop-response [request]
  (let [user   (user/current request)
        owner? (comp (-> user :inventory set) :id)]
    (if-let [products (seq (remove owner? (db/find-all :product :name)))]
      (->select-menu products)
      "There are no items available to purchase.")))

(defmethod slash/handle-slash "shop" [request]
  (interaction/reply-ephemeral! request (->shop-response request)))

(defmethod slash/handle-slash "shop-menu" [request]
  (interaction/reply-ephemeral! request "This is currently read-only!"))
