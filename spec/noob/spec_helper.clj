(ns noob.spec-helper
  (:require [c3kit.apron.time :as time]
            [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]
            [discord.interaction :as interaction]
            [noob.bot :as bot]
            [speclj.core :refer :all]))

(defn stub-discord []
  (redefs-around [discord-ws/status-update!      (stub :discord/status-update!)
                  interaction/reply!             (stub :discord/reply-interaction!)
                  interaction/reply-ephemeral!   (stub :discord/reply-interaction-ephemeral!)
                  interaction/edit-original!     (stub :discord/edit-original!)
                  interaction/create-message!    (stub :discord/create-message!)
                  interaction/embed!             (stub :discord/embed!)
                  discord-rest/get-current-user! (stub :discord/get-current-user!)]))

(defn stub-bot []
  (redefs-around [bot/gateway         (constantly :bot/gateway)
                  bot/events          (constantly :bot/events)
                  bot/rest-connection (constantly :bot/rest)
                  bot/init!           (stub :bot/init!)
                  bot/stop!           (stub :bot/stop!)]))

(defn stub-now [time]
  (redefs-around [time/now (stub :now {:return time})]))

(defmacro should-have-replied [request message]
  `(should-have-invoked :discord/reply-interaction! {:with [~request ~message]}))

(defmacro should-have-replied-ephemeral [request message]
  `(should-have-invoked :discord/reply-interaction-ephemeral! {:with [~request ~message]}))

(defmacro should-have-edited-message [request content]
  `(should-have-invoked :discord/edit-original! {:with [~request ~content]}))

(defmacro should-have-created-message [request message]
  `(should-have-invoked :discord/create-message! {:with [~request ~message]}))

(defmacro should-have-embedded [request embed]
  `(should-have-invoked :discord/embed! {:with [~request ~embed]}))
