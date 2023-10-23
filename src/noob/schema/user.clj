(ns noob.schema.user
  (:require [c3kit.apron.schema :as s]))

(def user
  {:kind       (s/kind :user)
   :id         s/id
   :discord-id {:type :string :db [:unique-value]}
   :xp         {:type :long}
   :niblets    {:type :long}
   :inventory  {:type [:ref]}
   :loadout    {:type [:ref]}
   })

(def all [user])
