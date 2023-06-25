(ns discord.components.fragment
  (:require [discord.components.component :as component]
            [discord.components.hiccup :as hiccup]))

(defmethod component/->component :<> [_ _ body]
  (map hiccup/<-hiccup body))
