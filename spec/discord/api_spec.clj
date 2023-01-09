(ns discord.api-spec
  (:require [clj-http.client :as http]
            [discord.api :as sut]
            [noob.config :as config]
            [speclj.core :refer :all]))

(describe "Discord API"
  (with-stubs)

  (around [it]
    (with-redefs [http/post (stub :post)]
      (it)))

  (context "reply-interaction!"
    (it "missing token"
      (sut/reply-interaction! {:id 1} "Some content")
      (should-not-have-invoked :post))

    (it "missing missing id"
      (sut/reply-interaction! {:token "abc"} "Some content")
      (should-not-have-invoked :post))

    (it "missing missing content"
      (sut/reply-interaction! {:id 1 :token "abc"} nil)
      (should-not-have-invoked :post))

    (it "posts message"
      (sut/reply-interaction! {:id 1 :token "abc"} "Some content")
      (should-have-invoked :post {:with ["https://discord.com/api/v10/interactions/1/abc/callback"
                                         {:form-params  {:type 4 :data {:content "Some content"}}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]})))


  (context "create-application-slash-command!"
    (it "missing name"
      (sut/create-global-slash-command! nil "my command")
      (should-not-have-invoked :post))

    (it "missing description"
      (sut/create-global-slash-command! "hello")
      (should-have-invoked :post {:with [(str "https://discord.com/api/v10/applications/" config/app-id "/commands")
                                         {:form-params  {:name "hello" :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]}))

    (it "empty options"
      (sut/create-global-slash-command! "hello" "world" [])
      (should-have-invoked :post {:with [(str "https://discord.com/api/v10/applications/" config/app-id "/commands")
                                         {:form-params  {:name "hello" :description "world" :options [] :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]}))

    (it "one option"
      (sut/create-global-slash-command! "hello" "world" [{:some :opt}])
      (should-have-invoked :post {:with [(str "https://discord.com/api/v10/applications/" config/app-id "/commands")
                                         {:form-params  {:name "hello" :description "world" :options [{:some :opt}] :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" (str "Bot " config/token)}}]})))
  )