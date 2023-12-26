(ns discord.bot-spec
  (:require [c3kit.apron.corec :as ccc]
            [clojure.core.async :as async]
            [discljord.connections :as discord-ws]
            [discljord.events :as discord-events]
            [discljord.messaging :as discord-rest]
            [discord.bot :as sut]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(describe "Discord Bot Service"
  (with-stubs)
  (spec-helper/stub-thread)
  (before (reset! sut/config {:token         "discord-token"
                              :intents       [:intent-1 :intent-2]
                              :event-handler :event-handler-fn}))

  (it "service"
    (let [{:keys [start stop]} sut/service]
      (should= 'discord.bot/start-service start)
      (should= 'discord.bot/stop-service stop)))

  (it "configure!"
    (sut/configure! :foo :bar :baz :buzz)
    (should= @sut/config {:foo :bar :baz :buzz}))

  (context "pump-messages"
    (it "ignores interrupt exceptions"
      (with-redefs [discord-events/message-pump! (fn [_ _] (throw (InterruptedException.)))]
        (should-not-throw (sut/pump-messages! nil nil))))

    (it "throws all other exceptions"
      (with-redefs [discord-events/message-pump! (fn [_ _] (throw (Exception.)))]
        (should-throw (sut/pump-messages! nil nil))))
    )

  (context "start-service"

    (redefs-around [discord-rest/start-connection! (stub :start-connection! {:return :rest-connection})
                    discord-rest/get-current-user! (stub :get-current-user! {:return (delay {:id "bot-id"})})
                    discord-ws/connect-bot!        (stub :connect-bot! {:return :ws-connection})
                    discord-events/message-pump!   (stub :message-pump!)
                    async/chan                     (fn [n] {:chan n})])

    (it "creates a websocket connection"
      (let [{:keys [events gateway]} (:discord/bot (sut/start-service {}))]
        (should= {:chan 100} events)
        (should-have-invoked :connect-bot! {:with ["discord-token" events :intents #{:intent-1 :intent-2}]})
        (should= :ws-connection gateway)))

    (it "pumps websocket to the event handler"
      (let [{:keys [events message-thread]} (:discord/bot (sut/start-service {}))]
        (should message-thread)
        (should-have-invoked :thread/->Thread)
        (should-have-invoked :thread/start {:with [message-thread]})
        (-> message-thread :task ccc/invoke)
        (should-have-invoked :message-pump! {:with [events :event-handler-fn]})))

    (it "does not pump messages when no event handler is provided"
      (swap! sut/config dissoc :event-handler)
      (let [{:keys [message-thread]} (:discord/bot (sut/start-service {}))]
        (should-be-nil message-thread)
        (should-not-have-invoked :message-pump!)
        (should-not-have-invoked :thread/start)
        (should-not-have-invoked :thread/->Thread)))

    (it "starts a rest connection"
      (let [{:keys [rest]} (:discord/bot (sut/start-service {}))]
        (should-have-invoked :start-connection! {:with ["discord-token"]})
        (should= :rest-connection rest)))

    (it "cache's the bot's user id"
      (let [{:keys [id rest]} (:discord/bot (sut/start-service {}))]
        (should-have-invoked :get-current-user! {:with [rest]})
        (should= "bot-id" id)))

    (it "preserves app state"
      (let [app (sut/start-service {:foo :bar :baz :buzz})]
        (should= :bar (:foo app))
        (should= :buzz (:baz app))
        (should-contain :discord/bot app)))
    )

  (context "stop-service"

    (redefs-around [discord-rest/stop-connection! (stub :stop-connection!)
                    discord-ws/disconnect-bot!    (stub :disconnect-bot!)
                    async/close!                  (stub :close!)])

    (it "shuts down rest connection"
      (let [app (sut/stop-service {:discord/bot {:rest :rest-connection}})]
        (should= {} app)
        (should-have-invoked :stop-connection! {:with [:rest-connection]})
        (should-not-have-invoked :disconnect-bot!)
        (should-not-have-invoked :close!)
        (should-not-have-invoked :thread/interrupt)))

    (it "closes the websocket connection"
      (let [app (sut/stop-service {:discord/bot {:gateway :ws-connection}})]
        (should= {} app)
        (should-have-invoked :disconnect-bot! {:with [:ws-connection]})
        (should-not-have-invoked :stop-connection!)
        (should-not-have-invoked :close!)
        (should-not-have-invoked :thread/interrupt)))

    (it "closes the event channel"
      (let [app (sut/stop-service {:discord/bot {:events :chan}})]
        (should= {} app)
        (should-have-invoked :close! {:with [:chan]})
        (should-not-have-invoked :disconnect-bot!)
        (should-not-have-invoked :stop-connection!)
        (should-not-have-invoked :thread/interrupt)))

    (it "shuts down the messaging thread"
      (let [app (sut/stop-service {:discord/bot {:message-thread :thread}})]
        (should= {} app)
        (should-have-invoked :thread/interrupt {:with [:thread]})))

    (it "does nothing"
      (let [app (sut/stop-service {})]
        (should= {} app)
        (should-not-have-invoked :close!)
        (should-not-have-invoked :disconnect-bot!)
        (should-not-have-invoked :stop-connection!)
        (should-not-have-invoked :thread/interrupt)))

    (it "preserves app state"
      (let [app (sut/stop-service {:discord/bot {} :foo :bar :baz :buzz})]
        (should= {:foo :bar :baz :buzz} app)))
    )
  )