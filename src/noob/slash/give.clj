(ns noob.slash.give
  (:require [discord.interaction :as interaction]
            [noob.slash.core :as slash]))

(defmethod slash/handle-slash "give" [request]
  ;(prn "request:" request)
  ;(prn "options:" (-> request :data :options))
  ;(interaction/reply-ephemeral! request "This is a secret message")
  )
