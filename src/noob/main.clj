(ns noob.main
  (:require [c3kit.apron.app :as app]
            [c3kit.apron.legend :as legend]
            [c3kit.apron.log :as log]
            [c3kit.apron.util :as util]
            [c3kit.bucket.api :as db]
            [discljord.events :as discord-events]
            [noob.bot :as bot]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.events.ready]
            [noob.slash.steal]
            [noob.schema.full :as schema.full]
            [noob.slash.attack]
            [noob.slash.give]
            [noob.slash.love]
            [noob.slash.recurrent]
            [noob.slash.shop]))

(defn start-db [] (app/start! [db/service]))

(defn wrap-error [f]
  (fn [& args]
    (try
      (apply f args)
      (catch Exception e
        (log/error e)))))

(def all-services [db/service])
(def refresh-services [])
(defn start-all [] (app/start! all-services))
(defn stop-all [] (app/stop! all-services))

(defn maybe-init-dev []
  (when config/local?
    (let [refresh-init (util/resolve-var 'c3kit.apron.refresh/init)]
      (refresh-init refresh-services "noob" ['noob.main]))))

(defn -main [& _]
  ;; TODO [BAC]: turn bot into a service
  (try
    (legend/init! schema.full/legend)
    (maybe-init-dev)
    (start-all)
    (bot/init! config/token)
    (discord-events/message-pump! (bot/events) (wrap-error events/handle-event))
    (finally (bot/stop!)))
  ;(.addShutdownHook (Runtime/getRuntime) (Thread. stop-all))
  ;(.addShutdownHook (Runtime/getRuntime) (Thread. shutdown-agents))
  )
