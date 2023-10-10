(ns noob.product
  (:require [c3kit.bucket.db :as db]))

(defn create! [name slot & {:as opts}]
  (db/tx
    (assoc opts
      :kind :product
      :slot slot
      :name name)))
