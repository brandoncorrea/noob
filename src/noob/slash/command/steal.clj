(ns noob.slash.command.steal
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
   "You can't steal from yourself, NOOB!"
   "You reach into your own pocket and find nothing but lint."
   "You break into a house and pick the place drier than the Grinch on Christmas. As you start to head home, you realize you broke into your own house."
   "You steal the radio out of your own car. Impressive!"
   ])

(def almost-success-messages
  [
   "%s sneaks by unnoticed and still fails to get a single cent."
   "%s over-rotated a lock, jamming it and making it inaccessible."
   "Months of meticulous planning lead %s to execute a flawless heist, stealing what they believe to be a collection of prized artwork. However, upon returning home, %s faces an unexpected twist: the real art had already been stolen, and they had unknowingly lifted a set of near-perfect replicas."
   "In a stroke of bad luck, %s successfully hijacks a train, expecting a significant payoff. But the triumph is fleeting when the train suddenly runs out of coal, coming to a standstill in a remote area."
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
      (<= reward 0) (interaction/reply! request (format (rand-nth almost-success-messages) (user/mention thief)))
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

(defmethod slash/handle-command "steal" [request]
  (let [thief-id  (user/discord-id request)
        victim-id (get-in request [:data :options :victim])]
    (if (= thief-id victim-id)
      (interaction/reply! request (format (rand-nth self-messages) (user/mention thief-id)))
      (attempt-steal! request thief-id victim-id))))
