(ns noob.slash.command.give
  (:require [discord.interaction :as interaction]
            [discord.option :as option]
            [noob.core :as core]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(defn give-niblets! [request from to amount]
  (user/transfer-niblets! from to amount)
  (interaction/reply! request (str (user/mention from) " gave " (user/mention to) " " (core/niblet-term amount) "!")))

(defmethod slash/handle-command "give" [request]
  (let [amount    (option/get-option request :amount)
        sender    (delay (user/current request))
        recipient (delay (user/find-or-create (option/get-option request :recipient)))]
    (cond
      (neg? amount) (interaction/reply-ephemeral! request "Are you trying to /steal Niblets?")
      (zero? amount) (interaction/reply-ephemeral! request "How many Niblets do you want to give?")
      (some-> @sender :niblets pos?) (give-niblets! request @sender @recipient amount)
      :else (interaction/reply-ephemeral! request "You don't have enough Niblets. LOL"))))
