(ns noob.config
  (:require [c3kit.apron.env :as env]))

(def discord {:dev-guild (env/env "DISCORD_DEV_GUILD")
              :app-id    (env/env "DISCORD_APP_ID")
              :token     (env/env "DISCORD_TOKEN")})

(def datomic {:impl         :datomic
              :uri          "datomic:dev://localhost:4334/noob"
              :partition    :noob
              :migration-ns 'noob.migrations
              :full-schema  'noob.schema.full/full-schema})

(def environment (env/env "ENVIRONMENT"))
(def local? (= "local" environment))

(def env
  {:discord discord
   :bucket  datomic})

(def bucket (:bucket env))
(def dev-guild (-> env :discord :dev-guild))
(def app-id (-> env :discord :app-id))
(def token (-> env :discord :token))
