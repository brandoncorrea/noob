(ns noob.schema.command
  (:require [c3kit.apron.schema :as s]))

(def command
  {:kind        (s/kind :command)
   :id          s/id
   :last-ran-at {:type :instant :db [:no-history]}
   :interval    {:type :keyword}
   :user        {:type :ref}
   })

(def all [command])
