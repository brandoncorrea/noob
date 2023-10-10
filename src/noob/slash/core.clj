(ns noob.slash.core
  (:require [c3kit.apron.log :as log]
            [discord.option :as option]
            [noob.core :as core]
            [noob.events.core :as events]))

(def dev-commands
  [
   ["shop" "Get in, loser. We're going shopping!"]
   ])

(def global-commands
  [
   ["daily" "Redeem your daily Niblets!"]
   ["weekly" "Redeem your weekly Niblets!"]
   ["give" "Give some niblets to that special someone"
    [(option/->user! "recipient" "That special someone <3")
     (option/->int! "amount" "The number of niblets to bestow")]]
   ])

(def slash-name (comp (some-fn :custom-id :name) :data))
(defmulti handle-slash slash-name)
(defmethod handle-slash :default [request]
  (log/debug (str "Unhandled slash command: " (slash-name request) " " (pr-str request))))

(defn normalize-options [request]
  (let [options (-> request :data :options)]
    (cond-> request
            options
            (assoc-in [:data :options] (core/->hash-map :name :value options)))))

(defmethod events/handle-event :interaction-create [_ request]
  (-> request normalize-options handle-slash))
