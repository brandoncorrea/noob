(ns noob.user
  (:require [c3kit.bucket.db :as db]))

(defn by-discord-id [id] (db/ffind-by :user :discord-id id))
(def discord-user (comp :user :member))
(def username (comp :username discord-user))
(def discord-id (comp :id discord-user))
(def current (comp by-discord-id discord-id))

(defn create [discord-id]
  {:kind       :user
   :id         (db/tempid)
   :discord-id discord-id})
(def create! (comp db/tx create))