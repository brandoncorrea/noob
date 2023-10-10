(ns noob.schema.full
  (:require [noob.schema.command :as command]
            [noob.schema.product :as product]
            [noob.schema.user :as user]))

(def legend
  {
   :user    user/user
   :command command/command
   :product product/product
   })

(def full-schema (vals legend))
