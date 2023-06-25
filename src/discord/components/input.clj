(ns discord.components.input
  (:require [discord.components.component :as component]
            [discord.core :as core]))

(defn wrap-length [{:keys [length min-length max-length] :as m}]
  (let [min-length (or min-length length)
        max-length (or max-length length)]
    (cond-> m
            min-length (assoc :min_length min-length)
            max-length (assoc :max_length max-length))))

(defn optional? [{:keys [required class-list] :as m}]
  (and (not required)
       (or (contains? m :required)
           (some #{"optional"} class-list))))

(defn wrap-required [m]
  (if (optional? m)
    (assoc m :required false)
    (dissoc m :required)))

(defmethod component/->component :input [_ options [label]]
  (-> options
      (core/assoc-unless :label label)
      wrap-length
      wrap-required
      (component/wrap-style {"short" 1 "paragraph" 2} 1)
      (select-keys [:custom_id :style :label :min_length :max_length :required :value :placeholder])
      (assoc :type 4)))
