(ns noob.repl
  (:require
    [c3kit.apron.log :as log]
    [c3kit.apron.time :as time :refer [milliseconds seconds minutes hours days ago from-now]]
    [c3kit.bucket.db :as db]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as str]
    [noob.main :as main]
    ))

(println "Welcome to the Noob REPL!")
(println "Initializing")
(main/start-db)
