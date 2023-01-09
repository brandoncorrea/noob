(ns noob.events.core
  (:require [c3kit.apron.log :as log]))

(defmulti handle-event (fn [type _] type))
(defmethod handle-event :default [type _]
  (log/info (str "Unhandled event type: " type)))
