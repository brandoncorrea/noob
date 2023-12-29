(ns noob.slash.command.shop
  (:require [c3kit.bucket.api :as db]
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
  [:tr (into menu-root (map ->option) products)])

(defn unowned-products [user]
  (let [owner? (comp (-> user user/inventory set) :id)]
    (->> (db/find-by :product :level ['<= (user/level user)])
         (remove owner?))))

(defn ->shop-response [user]
  (if-let [products (seq (unowned-products user))]
    (->shop-menu products)
    "There are no items available to purchase."))

(defmethod slash/handle-command "shop" [request]
  (interaction/reply! request (->shop-response (user/current request))))
