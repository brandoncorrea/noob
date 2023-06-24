(ns discord.components.button
  (:require [discord.components.core :as components]
            [discord.core :as core]))

(def styles
  {"primary"   1
   "secondary" 2
   "success"   3
   "danger"    4
   "link"      5})

(defn wrap-disabled [{:keys [class-list disabled] :as m}]
  (if (or disabled (some #{"disabled"} class-list))
    (assoc m :disabled true)
    (dissoc m :disabled)))

(defmethod components/->component :button [_ {:keys [href] :as options} [label]]
  (-> options
      (core/assoc-unless :url href)
      wrap-disabled
      (components/wrap-style styles 1)
      (core/assoc-unless :label label)
      (select-keys [:custom_id :style :disabled :label :url])
      (assoc :type 2)))
