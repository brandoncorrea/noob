(ns noob.slash.command.inventory
  (:require [c3kit.bucket.api :as db]
            [discord.interaction :as interaction]
            [noob.core :as core]
            [noob.slash.core :as slash]
            [noob.style.core :as style]
            [noob.user :as user]))

(defn ->descriptor [{:keys [name attack defense sneak perception level]}]
  (cond-> name
          attack (str " ⚔️ " attack)
          defense (str " 🛡 " defense)
          sneak (str " 🥷 " sneak)
          perception (str " 👁 " perception)
          level (str " ⭐️ " level)))

(defn inventory-description [inventory]
  (->> (map ->descriptor inventory)
       (apply core/join-lines)))

(defn describe-inventory [inventory member]
  {:title       "Inventory"
   :description (inventory-description inventory)
   :color       style/green
   :author      (user/->author member)})

(defn render-item [loadout {:keys [id name]}]
  (let [class (if (core/some= id loadout) "success" "primary")]
    [:button {:id id :class class} name]))

(defn render-inventory [inventory loadout]
  (->> (map #(render-item loadout %) inventory)
       (into [:<>])))

(defn realize! [ids] (map db/entity ids))

(defn display-inventory [request inventory loadout]
  (let [components (render-inventory inventory loadout)
        embed      (describe-inventory inventory (:member request))]
    (interaction/reply! request components :embed embed)))

(defmethod slash/handle-command "inventory" [request]
  (let [user      (user/current request)
        inventory (realize! (user/inventory user))
        loadout   (user/loadout user)]
    (if (seq inventory)
      (display-inventory request inventory loadout)
      (interaction/reply-ephemeral! request "Your inventory is empty."))))
