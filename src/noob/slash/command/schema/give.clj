(ns noob.slash.command.schema.give
  (:require [c3kit.apron.schema :as schema]))

(def schema
  {:recipient {:type     :string
               :validate schema/present?
               :api      {:type        :user
                          :required    true
                          :description "The recipient of your handout"}}
   :amount    {:type     :int
               :validate schema/present?
               :api      {:required true :description "The number of niblets to bestow"}}})

(def give
  {:name        "give"
   :description "Give some niblets to that special someone"
   :schema      schema})
