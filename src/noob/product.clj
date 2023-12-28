(ns noob.product
  (:require [c3kit.bucket.api :as db]
            [c3kit.bucket.jdbc]))

(def slot-names
  {
   :back      "Back"
   :chest     "Chest"
   :feet      "Feet"
   :hands     "Hands"
   :head      "Head"
   :legs      "Legs"
   :main-hand "Main Hand"
   :off-hand  "Off-Hand"
   })

(defn create! [name slot level price & {:as opts}]
  (db/tx
    (assoc opts
      :kind :product
      :slot slot
      :name name
      :price price
      :level level)))

(defn entity [id] (db/entity :product id))
(defn entities [ids] (map entity ids))
