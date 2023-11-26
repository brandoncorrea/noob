(ns noob.core
  (:require [c3kit.apron.schema :as s]
            [c3kit.bucket.jdbc :as jdbc]
            [c3kit.bucket.sqlite3]
            [clojure.string :as str]
            [noob.config :as config]))

(def auto-int-id-type
  (assoc-in s/id [:db :type] (jdbc/auto-int-primary-key (:dialect config/bucket))))

(defn ->hash-map [key-fn value-fn coll]
  (apply hash-map (mapcat (juxt key-fn value-fn) coll)))

(defn ** [n pow]
  (cond
    (pos? pow) (apply * 1 (repeat pow n))
    (neg? pow) (apply / 1 (repeat (- pow) n))
    :else 1))

(defn niblet-term [amount]
  (str amount " Niblet" (when (not= 1 amount) "s")))

(defn join-lines [& lines] (str/join "\n" lines))

(defn some= [thing coll] (some #(= thing %) coll))