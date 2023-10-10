(ns noob.migrations.m20231010
  (:require [c3kit.bucket.db :as db]))

(defn ->product [name level price slot & {:as options}]
  (assoc options :kind :product
                 :name name
                 :level level
                 :price price
                 :slot slot))

(defn migrate []
  (db/tx* [
           (->product "Water Gun" 1 100 :main-hand :attack 1)
           (->product "Cardboard Tube" 2 500 :main-hand :attack 2)
           (->product "Crowbar" 3 1000 :main-hand :attack 3)

           (->product "Stick" 1 100 :off-hand :attack 1 :description "It's brown and sticky.")
           (->product "Glow Stick" 1 100 :off-hand :perception 1)
           (->product "Magnifying Glass" 2 500 :off-hand :perception 2)

           (->product "Propeller Hat" 1 100 :head :perception 1)
           (->product "Paper Bag" 1 100 :head :defense 1 :description "Protects you and your dignity.")
           (->product "Groucho Glasses" 3 1000 :head :sneak 3)

           (->product "Bunny Slippers" 1 100 :feet :sneak 1)
           (->product "Sneakers" 2 500 :feet :sneak 2 :description "As the name implies, these help you sneak.")

           (->product "Mittens" 1 100 :hands :sneak 1)
           (->product "Diaper" 1 100 :legs :defense 1 :description "Gives you a little extra cushion.")
           ]))
