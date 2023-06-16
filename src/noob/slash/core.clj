(ns noob.slash.core
  (:require [clojure.tools.logging :as log]
            [noob.events.core :as events]))

(def dev-commands
  {
   "daily"  "Redeem your daily Niblets!"
   "weekly" "Redeem your weekly Niblets!"
   })

(def global-commands
  {})

(def slash-name (comp :name :data))
(defmulti handle-slash slash-name)
(defmethod handle-slash :default [data]
  (log/debug (str "Unhandled slash command: " (slash-name data) " " (pr-str data))))
(defmethod events/handle-event :interaction-create [_ data]
  (handle-slash data))
