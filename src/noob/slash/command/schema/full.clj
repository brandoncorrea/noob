(ns noob.slash.command.schema.full
  (:require [c3kit.apron.corec :as ccc]
            [discord.option :as option]
            [noob.slash.command.schema.attack :as attack]
            [noob.slash.command.schema.give :as give]
            [noob.slash.command.schema.love :as love]
            [noob.slash.command.schema.steal :as steal]))

(def daily {:name "daily" :description "Redeem your daily Niblets!"})
(def inventory {:name "inventory" :description "See your inventory!"})
(def shop {:name "shop" :description "Get in, loser. We're going shopping!"})
(def stats {:name "stats" :description "View your player stats."})
(def weekly {:name "weekly" :description "Redeem your weekly Niblets!"})

;; TODO [BAC]: Implement commands: help, inventory

(def legend
  {
   :attack    attack/attack
   :daily     daily
   :give      give/give
   :inventory inventory
   :love      love/love
   :shop      shop
   :stats     stats
   :steal     steal/steal
   :weekly    weekly
   })

(def full-schema (vals legend))

(defn compile-command [{:keys [name description schema]}]
  (ccc/remove-nils
    {:type        1
     :name        name
     :description description
     :options     (seq (map (fn [[k v]] (option/<-spec k v)) schema))}))

(def prod-commands (map compile-command (remove :dev? full-schema)))
(def dev-commands (map compile-command (filter :dev? full-schema)))
