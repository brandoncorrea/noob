(ns discord.interaction-spec
  (:require [clj-http.client :as http]
            [discord.interaction :as sut]
            [noob.config :as config]
            [speclj.core :refer :all]))

(describe "Discord Interactions"
  (with-stubs)
  (redefs-around [http/post (stub :post)])

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

  (context "reply-interaction!"
    (it "missing token"
      (sut/reply! {:id 1} "Some content")
      (should-not-have-invoked :post))

    (it "missing missing id"
      (sut/reply! {:token "abc"} "Some content")
      (should-not-have-invoked :post))

    (it "missing missing content"
      (sut/reply! {:id 1 :token "abc"} nil)
      (should-not-have-invoked :post))

    (it "posts message"
      (sut/reply! {:id 1 :token "abc"} "Some content")
      (should-have-invoked :post {:with ["https://discord.com/api/v10/interactions/1/abc/callback"
                                         {:form-params  {:type 4 :data {:content "Some content"}}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]}))

    (it "with flags"
      (sut/reply! {:id 1 :token "abc"} "Some content" :voice-message :cross-posted :urgent)
      (should-have-invoked :post {:with ["https://discord.com/api/v10/interactions/1/abc/callback"
                                         {:form-params  {:type 4
                                                         :data {:content "Some content"
                                                                :flags   (sut/->flag [:voice-message :cross-posted :urgent])}}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]})))

  (it "reply-ephemeral!"
    (sut/reply-ephemeral! {:id 1 :token "abc"} "Some content")
    (should-have-invoked :post {:with ["https://discord.com/api/v10/interactions/1/abc/callback"
                                       {:form-params  {:type 4
                                                       :data {:content "Some content"
                                                              :flags   (:ephemeral sut/flags)}}
                                        :content-type :json
                                        :headers      {"Authorization" (str "Bot " config/token)}}]}))
  )