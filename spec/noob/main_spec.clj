(ns noob.main-spec
  (:require [c3kit.apron.app :as app]
            [c3kit.bucket.api :as db]
            [discord.bot :as discord-bot]
            [noob.main :as sut]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Main"
  (with-stubs)
  (spec-helper/stub-bot)

  (it "initializes with bot token"
    (with-redefs [app/start!             (stub :app/start!)
                  shutdown-agents        (stub :shutdown-agents)
                  sut/add-shutdown-hook! (stub :add-shutdown-hook!)]
      (sut/-main)
      (should-have-invoked :app/start! {:with [[db/service discord-bot/service]]})
      (should-have-invoked :add-shutdown-hook! {:with [sut/stop-all]})
      (should-have-invoked :add-shutdown-hook! {:with [shutdown-agents]})))

  )
