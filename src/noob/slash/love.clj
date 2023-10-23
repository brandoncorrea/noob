(ns noob.slash.love
  (:require [discord.interaction :as interaction]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(def self-messages
  [
   "%s stares deeply into their eyes in the mirror, then intensely gives the mirror a big smooch. 😘",
   "%s treats themselves.",
   "%s's multiple personalities all come out at once to express extreme gratitude toward each other.",
   "%s throws themself a birthday party, inviting all their stuffed animals and imaginary friends."
   ])

(def others-messages
  [
   "%1$s whispers carelessly into %2$s's ear.",
   "%1$s hugs %2$s so tightly that they suffocate and die.",
   "%1$s stands outside %2$s's window holding a boombox in the air playing In Your Eyes.",
   "%1$s serenades %2$s with sweet sweet love songs by Barry White.",
   "%1$s walks a thousand miles to fall down at %2$s's door.",
   "%1$s loves %2$s (like a friend 😔)",
   "%1$s writes a letter to %2$s expressing their true love."
   ])

(defn create-message [lover beloved]
  (if (= lover beloved)
    (format (rand-nth self-messages) lover)
    (format (rand-nth others-messages) lover beloved)))

(defmethod slash/handle-name "love" [request]
  (let [lover   (user/mention (-> request :member :user :id))
        beloved (user/mention (get-in request [:data :options "beloved"]))]
    (interaction/reply! request (create-message lover beloved))))
