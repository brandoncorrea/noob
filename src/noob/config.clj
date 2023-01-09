(ns noob.config
  (:require [clojure.edn :as edn]))

(def config (-> "config.edn" slurp edn/read-string))
(def token (:token config))
(def app-id (:app-id config))
(def dev-guild (:dev-guild config))