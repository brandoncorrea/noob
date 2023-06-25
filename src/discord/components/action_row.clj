(ns discord.components.action-row
  (:require [discord.components.component :as component]
            [discord.components.hiccup :as hiccup]))

(defmethod component/->component :tr [_ _ body]
  {:type       1
   :components (map hiccup/<-hiccup body)})
