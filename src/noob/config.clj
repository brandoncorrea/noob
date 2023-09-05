(ns noob.config
  (:require [c3kit.apron.env :as env]))

(def env
  {:token     (env/env "DISCORD_TOKEN")
   :app-id    (env/env "DISCORD_APP_ID")
   :dev-guild (env/env "DISCORD_DEV_GUILD")
   })

(def token (:token env))
(def app-id (:app-id env))
(def dev-guild (:dev-guild env))
