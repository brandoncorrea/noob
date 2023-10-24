(ns discord.interaction-spec
  (:require [clj-http.client :as http]
            [discord.interaction :as sut]
            [noob.config :as config]
            [speclj.core :refer :all]))

(defn ->message-data [{:keys [content components] :as data}]
  (cond->> data
           (or content components)
           (merge {:components []})))

(defn ->request-options [form-params]
  {:form-params  form-params
   :content-type :json
   :headers      {"Authorization" "Bot bot-token"}})

(defmacro should-post-with-data [data]
  `(let [data# ~data]
     (should-have-invoked :post {:with ["https://discord.com/api/v10/interactions/1/abc/callback"
                                        (->request-options
                                          (cond-> {:type 4}
                                                  data# (assoc :data (->message-data data#))))]})))

(defmacro should-patch-with-data [data]
  `(should-have-invoked :patch {:with ["https://discord.com/api/v10/channels/bar/messages/foo"
                                       (->request-options (->message-data ~data))]}))

(def request)

(describe "Discord Interactions"
  (with-stubs)
  (redefs-around [http/post    (stub :post)
                  http/patch   (stub :patch)
                  config/token "bot-token"])

  (with request {:id 1 :token "abc"})

  (it "interaction flags"
    (should= 2r1 (:cross-posted sut/flags))
    (should= 2r10 (:cross-post sut/flags))
    (should= 2r100 (:suppress-embeds sut/flags))
    (should= 2r1000 (:source-message-deleted sut/flags))
    (should= 2r10000 (:urgent sut/flags))
    (should= 2r100000 (:has-thread sut/flags))
    (should= 2r1000000 (:ephemeral sut/flags))
    (should= 2r10000000 (:loading sut/flags))
    (should= 2r100000000 (:failed-to-mention-roles sut/flags))
    (should= 2r1000000000000 (:suppress-notifications sut/flags))
    (should= 2r10000000000000 (:voice-message sut/flags)))

  (it "reply-ephemeral!"
    (sut/reply-ephemeral! @request "Some content")
    (should-post-with-data {:content "Some content" :flags (:ephemeral sut/flags)}))

  (context "reply-interaction!"
    (it "missing token"
      (sut/reply! (dissoc @request :token) "Some content")
      (should-not-have-invoked :post))

    (it "missing id"
      (sut/reply! (dissoc @request :id) "Some content")
      (should-not-have-invoked :post))

    (it "missing content"
      (sut/reply! @request nil)
      (should-post-with-data nil))

    (it "posts message"
      (sut/reply! @request "Some content")
      (should-post-with-data {:content "Some content"}))

    (it "with flags"
      (sut/reply! @request "Some content" :flags [:voice-message :cross-posted :urgent])
      (should-post-with-data {:content "Some content"
                              :flags   (sut/->flag [:voice-message :cross-posted :urgent])}))

    (it "with hiccup"
      (sut/reply! @request [:select [:option "foo"]])
      (should-post-with-data {:components [{:type 1 :components [{:type 3 :options [{:label "foo"}]}]}]}))

    )

  (context "edit-original!"

    (it "missing message id"
      (sut/edit-original! {:message {:channel-id "bar"}} "Some content")
      (should-not-have-invoked :patch))

    (it "missing channel id"
      (sut/edit-original! {:message {:id "foo"}} "Some content")
      (should-not-have-invoked :patch))

    (it "patches message"
      (sut/edit-original! {:message {:id "foo" :channel-id "bar"}} "Some content")
      (should-patch-with-data {:content "Some content"}))

    (it "with hiccup"
      (sut/edit-original! {:message {:id "foo" :channel-id "bar"}} [:select [:option "foo"]])
      (should-patch-with-data {:components [{:type 1 :components [{:type 3 :options [{:label "foo"}]}]}]}))

    )

  (it "embed!"
    (sut/embed! @request {:some :embed})
    (should-post-with-data {:embeds [{:some :embed}]}))
  )
