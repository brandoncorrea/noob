(ns noob.slash.core
  (:require [c3kit.apron.log :as log]
            [noob.core :as core]
            [noob.events.core :as events]))

(def slash-name (comp :name :data))
(def custom-id (comp :custom-id :data))

(defmulti handle-name slash-name)
(defmulti handle-custom-id custom-id)

(defn maybe-debug [key-fn label request]
  (when-let [name (key-fn request)]
    (log/debug (str "Unhandled slash " label ": " name " " (pr-str request)))))
(defmethod handle-name :default [request] (maybe-debug slash-name "Name" request))
(defmethod handle-custom-id :default [request] (maybe-debug custom-id "custom id" request))

(defn normalize-options [request]
  (let [options (-> request :data :options)]
    (cond-> request
            options
            (assoc-in [:data :options] (core/->hash-map :name :value options)))))

;; TODO [BAC]: Simplify the interface between here and handle-name & handle-custom-id

(defmethod events/handle-event :interaction-create [_ request]
  (let [options (normalize-options request)]
    (handle-name options)
    (handle-custom-id options)))
