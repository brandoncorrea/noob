(ns noob.schema.full
  (:require [noob.schema.command :as command]
            [noob.schema.user :as user]))

(def full-schema [user/user command/all])
