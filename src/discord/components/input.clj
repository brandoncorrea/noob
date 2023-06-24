(ns discord.components.input
  (:require [discord.components.core :as components]
            [discord.core :as core]))

(defn wrap-length [{:keys [length min-length max-length] :as m}]
  (cond-> m
          length
          (cond-> (not min-length) (assoc :min-length length)
                  (not max-length) (assoc :max-length length))))

(defn optional? [{:keys [required class-list] :as m}]
  (and (not required)
       (or (contains? m :required)
           (some #{"optional"} class-list))))

(defn wrap-required [m]
  (if (optional? m)
    (assoc m :required false)
    (dissoc m :required)))

(defmethod components/->component :input [_ options [label]]
  (-> options
      (core/assoc-unless :label label)
      wrap-length
      wrap-required
      (components/wrap-style {"short" 1 "paragraph" 2} 1)
      (select-keys [:custom_id :style :label :min-length :max-length :required :value :placeholder])
      (assoc :type 4)))
