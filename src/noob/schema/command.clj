(ns noob.schema.command
  (:require [c3kit.apron.schema :as s]
            [noob.core :as core]))

(def command
  {:kind        (s/kind :command)
   :id          core/auto-int-id-type
   :last-ran-at {:type :instant :db [:no-history]}
   :interval    {:type :keyword}
   :user        {:type :ref}
   })

(def all [command])
