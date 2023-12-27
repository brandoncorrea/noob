(ns noob.slash.command.schema.love
  (:require [c3kit.apron.schema :as schema]))

(def schema
  {:beloved {:type     :string
             :validate schema/present?
             :api      {:type        :user
                        :required    true
                        :description "That special someone 🫶"}}})

(def love
  {:name        "love"
   :description "Love another player ❤️"
   :schema      schema})
