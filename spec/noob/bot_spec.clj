(ns noob.bot-spec
  (:require [clojure.core.async :as async]
            [discljord.connections :as discord-ws]
            [discljord.messaging :as discord-rest]
            [noob.bot :as sut]
            [speclj.core :refer :all]))

(defn clear-bot! []
  (reset! sut/state nil)
  (reset! sut/id nil))

(describe "Bot"
  (with-stubs)
  (before (clear-bot!))
  (after (clear-bot!))

  (around [it]
    (with-redefs [async/chan                     (fn [buf] {:chan buf})
                  async/close!                   (stub :async/close!)
                  discord-ws/connect-bot!        (stub :discord/connect-bot! {:return "gateway"})
                  discord-ws/disconnect-bot!     (stub :discord/disconnect-bot!)
                  discord-rest/start-connection! (stub :discord/start-connection! {:return "rest"})
                  discord-rest/stop-connection!  (stub :discord/stop-connection!)
                  discord-rest/get-current-user! (stub :discord/get-current-user! {:return (delay {:id 234})})]
      (it)))

  (it "initializes the bot state"
    (let [channel {:chan 100}]
      (sut/init! "TOKEN")
      (should-have-invoked :discord/connect-bot! {:with ["TOKEN" channel :intents #{:guild-messages}]})
      (should-have-invoked :discord/start-connection! {:with ["TOKEN"]})
      (should-have-invoked :discord/get-current-user! {:with ["rest"]})
      (should= 234 @sut/id)
      (should= "rest" (sut/rest-connection))
      (should= "gateway" (sut/gateway))
      (should= channel (sut/events))))

  (it "stops the bot connection"
    (sut/init! "TOKEN")
    (sut/stop!)
    (should-have-invoked :discord/stop-connection! {:with ["rest"]})
    (should-have-invoked :discord/disconnect-bot! {:with ["gateway"]})
    (should-have-invoked :async/close! {:with [{:chan 100}]}))
  )
