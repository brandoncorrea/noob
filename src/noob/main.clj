(ns noob.main
  (:require [c3kit.apron.app :as app]
            [c3kit.apron.legend :as legend]
            [c3kit.apron.util :as util]
            [c3kit.bucket.api :as db]
            [discord.bot :as discord-bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.ready]
            [noob.schema.full :as schema.full]
            [noob.slash.action.shop-menu]
            [noob.slash.command.attack]
            [noob.slash.command.give]
            [noob.slash.command.inventory]
            [noob.slash.command.love]
            [noob.slash.command.recurrent]
            [noob.slash.command.shop]
            [noob.slash.command.stats]
            [noob.slash.command.steal]))

(defn start-db [] (app/start! [db/service]))

(def all-services [db/service discord-bot/service])
(def refresh-services [])
(defn start-all [] (app/start! all-services))
(defn stop-all [] (app/stop! all-services))

(defn maybe-init-dev []
  (when config/local?
    (let [refresh-init (util/resolve-var 'c3kit.apron.refresh/init)]
      (refresh-init refresh-services "noob" ['noob.main 'discord.bot]))))

(def bot-config {:token         (:token config/discord)
                 :intents       [:guild-messages]
                 :event-handler events/event-handler})

(defn add-shutdown-hook! [^Runnable target]
  (.addShutdownHook (Runtime/getRuntime) (Thread. target)))

(defn -main []
  (legend/init! schema.full/legend)
  (maybe-init-dev)
  (discord-bot/configure! bot-config)
  (start-all)
  (run! add-shutdown-hook! [stop-all shutdown-agents]))
