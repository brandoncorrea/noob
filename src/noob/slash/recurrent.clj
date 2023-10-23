(ns noob.slash.recurrent
  (:require [c3kit.bucket.api :as db]
            [discord.interaction :as interaction]
            [noob.command :as command]
            [noob.slash.core :as slash]
            [noob.user :as user]))

(def MS_PER_MINUTE 60000)
(def MS_PER_HOUR (* MS_PER_MINUTE 60))
(def MS_PER_DAY (* MS_PER_HOUR 24))

(defn- format-leftover-time [ms]
  (let [days    (int (/ ms MS_PER_DAY))
        ms      (- ms (* days MS_PER_DAY))
        hours   (int (/ ms MS_PER_HOUR))
        ms      (- ms (* hours MS_PER_HOUR))
        minutes (int (/ ms MS_PER_MINUTE))
        [minutes? hours? days?] (map pos? [minutes hours days])]
    (str (when days? days)
         (cond (> days 1) " days"
               (= days 1) " day")
         (when (and days? hours? minutes?) ", ")
         (when (and days? hours? (not minutes?)) " and ")
         (when hours? hours)
         (cond (> hours 1) " hours"
               (= hours 1) " hour")
         (when (and days? hours? minutes?) ",")
         (when (and hours? minutes?) " and ")
         (when (pos? minutes) minutes)
         (cond (> minutes 1) " minutes"
               (= minutes 1) " minute"))))

(defn- not-ready-message [kind time-left]
  (str "Your " (name kind) " reward will be ready in " (format-leftover-time time-left) "!"))

(defn- gen-niblets [kind]
  (if (= :daily kind)
    (inc (rand-int 50))
    (+ 51 (rand-int 200))))

(defn- add-niblets [user kind]
  (let [niblets (gen-niblets kind)]
    (if (:niblets user)
      (update user :niblets + niblets)
      (assoc user :niblets niblets))))

(defn- award-niblets! [kind user command request]
  (let [user (db/tx (add-niblets user kind))]
    (command/bump-runtime! (or command (command/create-command kind user)))
    (interaction/reply! request (str (user/username request) " received " (:niblets user) " Niblets!"))))

(defn- handle-recurrent [kind request]
  (let [user      (user/find-or-create (user/discord-id request))
        command   (command/by-user kind user)
        time-left (command/millis-to-reset command)]
    (if (some-> time-left pos?)
      (interaction/reply! request (not-ready-message kind time-left))
      (award-niblets! kind user command request))))

(defmethod slash/handle-name "daily" [request] (handle-recurrent :daily request))
(defmethod slash/handle-name "weekly" [request] (handle-recurrent :weekly request))
