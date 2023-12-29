(ns noob.slash.command.help
  (:require [clojure.string :as str]
            [discord.interaction :as interaction]
            [noob.slash.command.schema.full :as command-schema]
            [noob.slash.core :as slash]
            [noob.style.core :as style]))

(defn describe-command [{:keys [name description]}]
  (str "/" name "\n" description))

(defn commands->help-message [commands]
  (if (seq commands)
    (str/join "\n\n" (map describe-command commands))
    "LOL NOOB there's nothing to show here."))

(def help-embed
  {:title       "Help!"
   :description (commands->help-message command-schema/prod-commands)
   :color       style/green})

(defmethod slash/handle-command "help" [request]
  (interaction/embed! request help-embed))
