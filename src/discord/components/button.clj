(ns discord.components.button
  (:require [discord.components.component :as component]
            [discord.core :as core]))

(def styles
  {"primary"   1
   "secondary" 2
   "success"   3
   "danger"    4
   "link"      5})

(defmethod component/->component :button [_ {:keys [href] :as options} [label]]
  (-> options
      (core/assoc-unless :url href)
      component/wrap-disabled
      (component/wrap-style styles 1)
      (core/assoc-unless :label label)
      (select-keys [:custom_id :style :disabled :label :url])
      (assoc :type 2)))
