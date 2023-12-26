(ns noob.slash.schema.attack
  (:require [c3kit.apron.schema :as schema]))

(def schema
  {:target {:type     :string
            :validate schema/present?
            :api      {:type        :user
                       :required    true
                       :description "The person you want to attack."}}})

(def attack
  {:name        "attack"
   :description "Attack another player!"
   :schema      schema})
