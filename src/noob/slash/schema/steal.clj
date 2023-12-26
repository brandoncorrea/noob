(ns noob.slash.schema.steal
  (:require [c3kit.apron.schema :as schema]))

(def schema
  {:victim {:type     :string
            :validate schema/present?
            :api      {:type        :user
                       :required    true
                       :description "The person you will be stealing from."}}})

(def steal
  {:name        "steal"
   :description "Steal Niblets from another player!"
   :schema      schema})
