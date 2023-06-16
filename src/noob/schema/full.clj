(ns noob.schema.full
  (:require [noob.schema.command :as command]
            [noob.schema.user :as user]))

(def legend
  {
   :user    user/user
   :command command/command
   })

(def full-schema (vals legend))
