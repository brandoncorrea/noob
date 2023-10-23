(ns noob.slash.core
  (:require [c3kit.apron.log :as log]
            [discord.option :as option]
            [noob.core :as core]
            [noob.events.core :as events]))

(def dev-commands [])

;; TODO [BAC]: Implement commands: steal, stats, help, inventory

(def global-commands
  [
   ["attack" "Attack another player!" [(option/->user! "target" "The person you want to attack.")]]
   ["daily" "Redeem your daily Niblets!"]
   ["give" "Give some niblets to that special someone"
    [(option/->user! "recipient" "The recipient of your handout")
     (option/->int! "amount" "The number of niblets to bestow")]]
   ["love" "Love another player ❤️"
    [(option/->user! "beloved" "That special someone 🫶")]]
   ["shop" "Get in, loser. We're going shopping!"]
   ["weekly" "Redeem your weekly Niblets!"]
   ])

(def slash-name (comp :name :data))
(def custom-id (comp :custom-id :data))

(defmulti handle-name slash-name)
(defmulti handle-custom-id custom-id)

(defn maybe-debug [key-fn label request]
  (when-let [name (key-fn request)]
    (log/debug (str "Unhandled slash " label ": " name " " (pr-str request)))))
(defmethod handle-name :default [request] (maybe-debug slash-name "Name" request))
(defmethod handle-custom-id :default [request] (maybe-debug custom-id "custom id" request))

(defn normalize-options [request]
  (let [options (-> request :data :options)]
    (cond-> request
            options
            (assoc-in [:data :options] (core/->hash-map :name :value options)))))

(defmethod events/handle-event :interaction-create [_ request]
  (let [options (normalize-options request)]
    (handle-name options)
    (handle-custom-id options)))
