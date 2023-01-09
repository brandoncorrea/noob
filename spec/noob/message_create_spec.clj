(ns noob.message-create-spec
  (:require [discljord.formatting :as formatting]
            [noob.bot :as bot]
            [noob.events :as events]
            [noob.message-create :as sut]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]
            [speclj.stub :as stub]))

(defn handle-message [channel-id author mentions]
  (->> {:channel-id channel-id
        :author     author
        :mentions   mentions}
       (events/handle-event :message-create)))

(describe "Message Create Event"
  (with-stubs)
  (spec-helper/stub-discord)
  (spec-helper/stub-bot)
  (before (reset! bot/id 456))
  (after (reset! bot/id nil))

  (it "no mentions"
    (handle-message 1 {:id 123} [])
    (should-not-have-invoked :discord/create-message!))

  (it "uninitialized bot"
    (reset! bot/id nil)
    (handle-message 1 {:id 123} [{:id 456}])
    (should-not-have-invoked :discord/create-message!))

  (it "does not mention our bot"
    (handle-message 1 {:id 123} [{:id 356}])
    (should-not-have-invoked :discord/create-message!))

  (it "mentions our bot"
    (handle-message 1 {:id 123} [{:id 456}])
    (let [[connection channel-id param content] (stub/last-invocation-of :discord/create-message!)]
      (should= :bot/rest connection)
      (should= 1 channel-id)
      (should= :content param)
      (should-contain content (map (fn [s] (str s ", " (formatting/mention-user {:id 123}) "!")) sut/responses))))
  )
