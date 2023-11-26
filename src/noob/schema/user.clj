(ns noob.schema.user
  (:require [c3kit.apron.schema :as s]
            [noob.core :as core]))

(def user
  {:kind       (s/kind :user)
   :id         core/auto-int-id-type
   :discord-id {:type :string :db [:unique-value]}
   :xp         {:type :long}
   :niblets    {:type :long}
   :inventory  {:type :string}
   :loadout    {:type :string}
   })

(def all [user])
