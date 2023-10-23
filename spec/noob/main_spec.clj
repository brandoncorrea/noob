(ns noob.main-spec
  (:require [c3kit.apron.app :as app]
            [c3kit.bucket.api :as db]
            [discljord.events :as discord-events]
            [noob.config :as config]
            [noob.events.core :as events]
            [noob.main :as sut]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Main"
  (with-stubs)
  (spec-helper/stub-bot)

  (it "initializes with bot token"
    (with-redefs [discord-events/message-pump! (stub :discord/message-pump!)
                  app/start!                   (stub :app/start!)
                  sut/wrap-error               identity]
      (sut/-main)
      (should-have-invoked :app/start! {:with [[db/service]]})
      (should-have-invoked :bot/init! {:with [config/token]})
      (should-have-invoked :discord/message-pump! {:with [:bot/events events/handle-event]})
      (should-have-invoked :bot/stop!)))
  )
