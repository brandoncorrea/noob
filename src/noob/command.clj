(ns noob.command
  (:require [c3kit.apron.time :as time]
            [c3kit.bucket.api :as db]))

(defn create-command
  ([interval user] (create-command interval user (time/now)))
  ([interval user last-ran-at]
   {:kind        :command
    :user        (:id user)
    :last-ran-at last-ran-at
    :interval    interval}))

(def create-command! (comp db/tx create-command))
(def create-daily-command! (partial create-command! :daily))
(def create-weekly-command! (partial create-command! :weekly))
(defn bump-runtime! [command] (db/tx command :last-ran-at (time/now)))
(defn by-user [interval user] (db/ffind-by :command :interval interval :user (:id user)))

(defn millis-to-reset [{:keys [last-ran-at interval]}]
  (some-> last-ran-at
          (time/after (time/days (interval {:daily 1 :weekly 7})))
          (time/millis-between (time/now))))
