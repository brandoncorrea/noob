(ns noob.events.core
  (:require [c3kit.apron.log :as log]))

(defmulti handle-event (fn [type _] type))
(defmethod handle-event :default [type _]
  (log/info (str "Unhandled event type: " type)))

(defn wrap-error [f]
  (fn [& args]
    (try
      (apply f args)
      (catch Exception e
        (log/error e)))))

(def event-handler (wrap-error handle-event))
