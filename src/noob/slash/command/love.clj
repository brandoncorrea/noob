(ns noob.slash.command.love
  (:require [discord.interaction :as interaction]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(def self-messages
  [
   "%s stares deeply into their eyes in the mirror, then intensely gives the mirror a big smooch. 😘"
   "%s treats themselves."
   "%s's multiple personalities all come out at once to express extreme gratitude toward each other."
   "%s throws themself a birthday party, inviting all their stuffed animals and imaginary friends."
   "%s dances like no one's watching, because no one is."
   "%s stages a dramatic one-person play in the living room, giving themselves a standing ovation."
   "%s holds a staring contest with themselves in the mirror, and somehow still loses."
   "%s sings a duet with themselves, taking turns between high and low parts."
   "%s takes selfies with a stuffed animal, captioning it 'Just hanging with my BFF.'"
   "%s attempts to set a world record for 'longest time spinning a pen,' and gives themselves a trophy."
   "%s writes a series of haikus about for themselves, each one more dramatic than the last."
   "%s builds a fort out of pillows and blankets, declaring themselves ruler of their lonely kingdom."
   "%s buys themselves a bouquet of roses, because who else will?"
   "%s rents a limo for an hour just to feel like a VIP, even if it's only for themselves."
   "%s makes a mixtape of love songs and dedicates it to the most important person: themselves."
   "%s prepares breakfast in bed, then crawls back under the covers to enjoy it."
   "%s sets up a candlelit bath, rose petals and all, to soak away their cares."
   "%s creates a vision board filled with all the things they love about themselves."
   "%s does a movie marathon of all their favorite rom-coms, laughing and crying freely."
   "%s pens a heartfelt love letter, then reads it aloud to themselves in the mirror."
   ])

(def others-messages
  [
   "%1$s trips over their own feet, only to be caught by %2$s."
   "%1$s tries to impress %2$s with a magic trick but pulls out a rubber chicken instead of a rabbit."
   "%1$s whispers carelessly into %2$s's ear."
   "%1$s makes spaghetti just to reenact Lady and the Tramp with %2$s."
   "%1$s dedicates a karaoke song to %2$s but forgets the lyrics halfway."
   "%1$s sends %2$s a bouquet of roses, only to find out %2$s is allergic."
   "%1$s hugs %2$s so tightly that they suffocate and die."
   "%1$s plans a surprise date for %2$s at the place they first met—a laundromat."
   "%1$s stands outside %2$s's window holding a boombox in the air playing In Your Eyes."
   "%1$s serenades %2$s with sweet sweet love songs by Barry White."
   "%1$s walks a thousand miles to fall down at %2$s's door."
   "%1$s loves %2$s (like a friend 😔)"
   "%1$s names a star after %2$s, only to find out it's actually a satellite."
   "%1$s gets a tattoo of %2$s's name, only to spell it wrong."
   "%1$s tries to impress %2$s with a daring skateboard trick and ends up in a cast."
   "%1$s buys a billboard to declare their love for %2$s, but it's in the wrong city."
   "%1$s learns to play the violin for %2$s but sounds like a dying cat."
   "%1$s handwrites a 20-page love letter to %2$s and then realizes they sent it to the wrong address."
   "%1$s writes a letter to %2$s expressing their true love."
   ])

(defn create-message [lover beloved]
  (if (= lover beloved)
    (format (rand-nth self-messages) lover)
    (format (rand-nth others-messages) lover beloved)))

(defmethod slash/handle-command "love" [request]
  (let [lover   (user/mention (user/discord-id request))
        beloved (user/mention (get-in request [:data :options :beloved]))]
    (interaction/reply! request (create-message lover beloved))))
