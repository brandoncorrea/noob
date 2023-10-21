(ns noob.config
  (:require [c3kit.apron.env :as env]))

(def token (env/env "DISCORD_TOKEN"))
(def app-id (env/env "DISCORD_APP_ID"))
(def dev-guild (env/env "DISCORD_DEV_GUILD"))
(def environment (env/env "ENVIRONMENT"))
(def local? (= "local" environment))
