(ns noob.spec-helper
  (:require [c3kit.apron.time :as time]
            [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]
            [discord.bot :as bot]
            [discord.interaction :as interaction]
            [discord.thread :as thread]
            [speclj.core :refer :all]))

(defn stub-discord []
  (redefs-around [discord-ws/status-update!      (stub :discord/status-update!)
                  interaction/reply!             (stub :discord/reply-interaction!)
                  interaction/reply-ephemeral!   (stub :discord/reply-interaction-ephemeral!)
                  interaction/edit-original!     (stub :discord/edit-original!)
                  interaction/update-message!    (stub :discord/update-message!)
                  interaction/create-message!    (stub :discord/create-message!)
                  interaction/embed!             (stub :discord/embed!)
                  discord-rest/get-current-user! (stub :discord/get-current-user!)]))

(defn stub-bot []
  (redefs-around [bot/gateway         (constantly :bot/gateway)
                  bot/events          (constantly :bot/events)
                  bot/rest-connection (constantly :bot/rest)]))

(defn stub-now [time]
  (redefs-around [time/now (stub :now {:return time})]))

(defmacro should-have-replied [request content & {:as options}]
  `(if-let [options# ~options]
     (should-have-invoked :discord/reply-interaction! {:with [~request ~content options#]})
     (should-have-invoked :discord/reply-interaction! {:with [~request ~content]})))

(defmacro should-have-replied-ephemeral [request message]
  `(should-have-invoked :discord/reply-interaction-ephemeral! {:with [~request ~message]}))

(defmacro should-have-edited-message [request content & {:as options}]
  `(if-let [options# ~options]
     (should-have-invoked :discord/edit-original! {:with [~request ~content options#]})
     (should-have-invoked :discord/edit-original! {:with [~request ~content]})))

(defmacro should-have-updated-message [request content & {:as options}]
  `(if-let [options# ~options]
     (should-have-invoked :discord/update-message! {:with [~request ~content options#]})
     (should-have-invoked :discord/update-message! {:with [~request ~content]})))

(defmacro should-have-created-message [request message]
  `(should-have-invoked :discord/create-message! {:with [~request ~message]}))

(defmacro should-have-embedded [request embed]
  `(should-have-invoked :discord/embed! {:with [~request ~embed]}))

(declare rec-merge)
(defn deep-merge [v & vs] (reduce rec-merge v vs))
(defn- rec-merge [v1 v2]
  (if (and (map? v1) (map? v2))
    (merge-with deep-merge v1 v2)
    (or v2 v1)))

(defn ->slash-request [command user & {:as options}]
  (deep-merge
    {:data   {:name command}
     :member {:user {:id (:discord-id user)}}}
    options))

(defn stub-thread []
  (redefs-around [thread/->Thread  (stub :thread/->Thread {:invoke (fn [task] {:task task})})
                  thread/start     (stub :thread/start)
                  thread/interrupt (stub :thread/interrupt)]))
