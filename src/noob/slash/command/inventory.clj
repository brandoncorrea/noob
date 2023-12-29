(ns noob.slash.command.inventory
  (:require [discord.interaction :as interaction]
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
    [:button {:id (str "inventory-button-" id) :class class} name]))

;; TODO [BAC]: We can only display up to 25 items. (5 rows with 5 buttons)
;;   This needs to be addressed at some point.
;;   - Paging, inventory limit, etc.

(defn render-inventory [inventory loadout]
  (->> (map #(render-item loadout %) inventory)
       (partition-all 5)
       (map #(into [:tr] %))
       (into [:<>])))

(defn ->inventory-content [request user]
  (let [inventory  (user/inventory! user)
        loadout    (user/loadout user)
        components (render-inventory inventory loadout)
        embed      (describe-inventory inventory (:member request))]
    [components {:embed embed}]))

(defn display-inventory [request user]
  (let [[content options] (->inventory-content request user)]
    (interaction/reply! request content options)))

(defmethod slash/handle-command "inventory" [request]
  (let [user (user/current request)]
    (if (seq (user/inventory user))
      (display-inventory request user)
      (interaction/reply-ephemeral! request "Your inventory is empty."))))
