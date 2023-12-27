(ns noob.slash.command.attack
  (:require [discord.interaction :as interaction]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(def success-messages
  [
   "%1$s just beat the living daylights out of %2$s!"
   "%1$s shoved %2$s off the edge of a cliff."
   "%1$s knocked %2$s into the middle of next week!"
   "%1$s smacked %2$s upside the head!"
   "%1$s duct-taped %2$s to a rocket and launched them to the moon!"
   "%1$s trapped %2$s in an endless loop of dad jokes, causing their brain to short-circuit."
   "%1$s pulled the rug out from under %2$s, sending them tumbling into a pit of rubber ducks."
   "%1$s challenged %2$s to a thumb war and won in record time."
   "%1$s summoned a flock of angry geese, leaving %2$s running for the hills."
   "%1$s caught %2$s in an oversized mousetrap, complete with cheese."
   "%1$s initiated a dance-off and gyrated so ferociously that %2$s got swallowed by the dance floor."
   "%1$s summoned a herd of plush unicorns that stampeded over %2$s."
   "%1$s force-fed %2$s a glitter-grenade. Now every burp, sneeze, and fart is forever followed by a small cloud of glitter."
   "%1$s fires a cannon loaded with rubber chickens—each outfitted with menacingly sharp teeth—at %2$s, causing sheer bewilderment and defeat."
   ])

(def fail-messages
  [
   "%1$s tried attacking %2$s and got PWND!"
   "%1$s went to do a roundhouse kick on %2$s, but fell on their face instead."
   "%1$s's plan to train an army of bees to sting %2$s dreadfully backfired."
   "%1$s accidentally gave %2$s the wrong cup of wine and wound up drinking the poison."
   "%1$s to fire an arrow at %2$s, but it boomerangs back, and hits %1$s directly in the loins."
   "Just as %1$s is about to pull the trigger with %2$s in their sights, %1$s is mauled by a bear who had just consumed copious amounts of cocaine."
   "%1$s audaciously challenges %2$s to an epic duel, only to lose in spectacular fashion because they were wearing the wrong day's underwear."
   "%1$s attempts to travel back in time to thwart %2$s, but miscalculates and lands in the middle of the Black Plague."
   "%1$s pulls out a Pokeball to capture %2$s but accidentally releases a Magikarp that flops around aimlessly."
   "%1$s attempts to 'Avada Kedavra' %2$s but held their wand backwards cast the spell on themselves instead."
   "%1$s prepares to unleash their 'Dragonborn Shout' on %2$s but ends up just coughing and spluttering."
   "%1$s channels their inner Neo, attempting a slow-motion bullet dodge against %2$s, but they instead get shot 27 times in the face."
   "%1$s attempts a flying karate kick aimed at %2$s, but misjudges the distance and sails past them, landing in a dumpster."
   "%1$s pulls out a ninja star to throw at %2$s, but it gets stuck to their hand and they can't throw it."
   "%1$s attempts to 'go medieval' on %2$s with a mace, but it's too heavy and they drop it on their own foot."
   ])

(def self-messages
  [
   "Self harm is NOT okay 😭❤️"
   "Whoa, whoa, whoa! Friendly fire, friendly fire!"
   "Whoa! You can't duel with your reflection!"
   "%s can't even friendly fire correctly."
   "Taking aim at yourself? That's like trying to fail a tutorial level!"
   "Trying to duel yourself? What's next, forgetting how to respawn?"
   "%s threw a punch and missed...their own face. Classic!"
   "%s attempted to ambush themselves, but they saw it coming."
   ])

(defn success? [attacker target]
  (< (user/roll target :defense)
     (* (user/roll attacker :attack) 0.75)))

(defn battle-results [attacker target]
  (if (success? attacker target)
    [attacker target success-messages 25]
    [target attacker fail-messages 15]))

(defn fight! [request attacker-id target-id]
  (let [attacker  (user/find-or-create attacker-id)
        target    (user/find-or-create target-id)
        [winner loser messages xp-factor] (battle-results attacker target)
        old-level (user/level winner)
        winner    (user/gain-xp! winner xp-factor (user/level loser))
        new-level (user/level winner)]
    (interaction/reply! request (format (rand-nth messages) (user/mention attacker) (user/mention target)))
    (when (< old-level new-level)
      (interaction/create-message! request (str (user/mention winner) " has reached level " new-level "!")))))

(defmethod slash/handle-command "attack" [request]
  (let [attacker-id (user/discord-id request)
        target-id   (-> request :data :options :target)]
    (if (= attacker-id target-id)
      (interaction/reply! request (format (rand-nth self-messages) (user/mention attacker-id)))
      (fight! request attacker-id target-id))))
