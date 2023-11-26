(ns discord.components.core
  "The glue that holds all the components together"
  (:require [discord.components.action-row]
            [discord.components.button]
            [discord.components.fragment]
            [discord.components.hiccup :as hiccup]
            [discord.components.input]
            [discord.components.option]
            [discord.components.select]))

(defn <-hiccup [hiccup] (hiccup/<-hiccup hiccup))

(defn hiccup->components [hiccup]
  (if (= :<> (first hiccup))
    (mapv <-hiccup (rest hiccup))
    [(<-hiccup hiccup)]))
