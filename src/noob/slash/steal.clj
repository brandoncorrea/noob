(ns noob.slash.steal
  (:require [discord.interaction :as interaction]
            [noob.roll :as roll]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(def fail-messages
  [
   "%1$s was caught trying to steal from %2$s. What a noob!"
   "%1$s sneezed on %2$s's face while trying to steal from them!"
   "%1$s stepped on a lego while sneaking around %2$s, letting out a bellowing shriek."
   "%1$s stubbed their little toe on %2$s's garden gnomes."
   "%1$s fell up the stairs while sneaking about %2$s's home."
   "As %1$s reached into %2$s's pocket, their hand was bitten by %2$s's dentures."
   "%1$s almost got away with robbing %2$s, but their impulsive humming gave them away."
   ])

(def self-messages
  [
   "You want to steal from... yourself?!"
   ])

(def almost-success-messages
  [
   "You snuck by unnoticed, but still failed to get a single penny."
   ])

(defn roll-theft [theft-fn thief victim]
  (theft-fn (user/level thief) (user/ability-score thief :sneak)
            (user/level victim) (user/ability-score victim :perception)))

(defn success? [thief victim] (roll-theft roll/steal? thief victim))
(defn theft-reward [thief victim] (roll-theft roll/stolen-niblets thief victim))

(defn steal! [request thief victim]
  (let [wallet (:niblets victim 0)
        reward (min (theft-reward thief victim) wallet)]
    (cond
      (<= wallet 0) (interaction/reply-ephemeral! request "There are no Niblets to steal :(")
      (<= reward 0) (interaction/reply-ephemeral! request (rand-nth almost-success-messages))
      :else (do (user/transfer-niblets! victim thief reward)
                (interaction/reply-ephemeral! request (str "You stole " reward " Niblets 😈"))))))

(defn fail! [request thief victim]
  (interaction/reply! request (format (rand-nth fail-messages) (user/mention thief) (user/mention victim)))
  (let [wallet (:niblets thief 0)
        reward (* 3 (max (theft-reward thief victim) 0))]
    (when (and (pos? wallet) (pos? reward))
      (user/transfer-niblets! thief victim reward)
      (interaction/create-message! request (str (user/mention thief) " pays a " reward " Niblet fine!")))))

(defn attempt-steal! [request thief-id victim-id]
  (let [thief  (user/find-or-create thief-id)
        victim (user/find-or-create victim-id)]
    (if (success? thief victim)
      (steal! request thief victim)
      (fail! request thief victim))))

(defmethod slash/handle-name "steal" [request]
  (let [thief-id  (-> request :member :user :id)
        victim-id (get-in request [:data :options "victim"])]
    (if (= thief-id victim-id)
      (interaction/reply! request (format (rand-nth self-messages) (user/mention thief-id)))
      (attempt-steal! request thief-id victim-id))))
