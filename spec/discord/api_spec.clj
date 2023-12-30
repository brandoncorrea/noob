(ns discord.api-spec
  (:require [clj-http.client :as http]
            [discord.api :as sut]
            [noob.config :as config]
            [speclj.core :refer :all]
            [speclj.stub :as stub]))

(describe "Discord API"
  (with-stubs)
  (redefs-around [http/post     (stub :post)
                  http/get      (stub :get)
                  http/put      (stub :put)
                  http/delete   (stub :delete)
                  config/app-id "app-id"
                  config/token  "bot-token"])

  (context "create-application-slash-command!"
    (it "missing name"
      (sut/create-global-slash-command! {:description "my command"})
      (should-not-have-invoked :post))

    (it "missing description"
      (sut/create-global-slash-command! {:name "hello"})
      (should-have-invoked :post {:with ["https://discord.com/api/v10/applications/app-id/commands"
                                         {:form-params  {:name "hello" :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" "Bot bot-token"}}]}))

    (it "empty options"
      (sut/create-global-slash-command! {:name "hello" :description "world" :options []})
      (should-have-invoked :post {:with ["https://discord.com/api/v10/applications/app-id/commands"
                                         {:form-params  {:name "hello" :description "world" :options [] :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" "Bot bot-token"}}]}))

    (it "one option"
      (sut/create-global-slash-command! {:name "hello" :description "world" :options [{:some :opt}]})
      (should-have-invoked :post {:with ["https://discord.com/api/v10/applications/app-id/commands"
                                         {:form-params  {:name "hello" :description "world" :options [{:some :opt}] :type 1}
                                          :content-type :json
                                          :headers      {"Authorization" "Bot bot-token"}}]})))

  (context "guild-preview"

    (it "http"
      (sut/guild-preview 123)
      (let [[url options] (stub/last-invocation-of :get)]
        (should= "https://discord.com/api/v10/guilds/123/preview" url)
        (should= :json (:as options))
        (should= "Bot bot-token" (get-in options [:headers "Authorization"]))))
    )

  (it "delete-guild-slash-command!"
    (sut/delete-guild-slash-command! 123 456)
    (should-have-invoked :delete {:with ["https://discord.com/api/v10/applications/app-id/guilds/123/commands/456"
                                         {:headers {"Authorization" "Bot bot-token"}}]}))

  (it "delete-global-slash-command!"
    (sut/delete-global-slash-command! 234)
    (should-have-invoked :delete {:with ["https://discord.com/api/v10/applications/app-id/commands/234"
                                         {:headers {"Authorization" "Bot bot-token"}}]}))

  (context "get-guild-commands"

    (it "http"
      (sut/get-guild-commands 123)
      (let [[url options] (stub/last-invocation-of :get)]
        (should= "https://discord.com/api/v10/applications/app-id/guilds/123/commands" url)
        (should= :json (:as options))
        (should= "Bot bot-token" (get-in options [:headers "Authorization"]))))

    (it "non-200"
      (with-redefs [http/get (constantly {:status 234 :body [:foo :bar]})]
        (should= [] (sut/get-guild-commands 123))))

    (it "success"
      (with-redefs [http/get (constantly {:status 200 :body [:foo :bar]})]
        (should= [:foo :bar] (sut/get-guild-commands 123))))
    )

  (it "guilds"
    (with-redefs [http/get (stub :get {:return {:status 200 :body [:foo]}})]
      (should= [:foo] (sut/guilds))
      (should-have-invoked :get {:with ["https://discord.com/api/v10/users/@me/guilds" {:as :json :headers {"Authorization" "Bot bot-token"}}]})))

  (it "overwrite guild commands"
    (sut/overwrite-guild-commands "foo-id" [:command-1 :command-2])
    (let [url     "https://discord.com/api/v10/applications/app-id/guilds/foo-id/commands"
          options {:form-params  [:command-1 :command-2]
                   :content-type :json
                   :headers      {"Authorization" "Bot bot-token"}}]
      (should-have-invoked :put {:with [url options]})))

  (context "get-global-commands"

    (it "http"
      (sut/get-global-commands)
      (let [[url options] (stub/last-invocation-of :get)]
        (should= "https://discord.com/api/v10/applications/app-id/commands" url)
        (should= :json (:as options))
        (should= "Bot bot-token" (get-in options [:headers "Authorization"]))))

    (it "non-200"
      (with-redefs [http/get (constantly {:status 234 :body [:foo :bar]})]
        (should= [] (sut/get-global-commands))))

    (it "success"
      (with-redefs [http/get (constantly {:status 200 :body [:foo :bar]})]
        (should= [:foo :bar] (sut/get-global-commands)))))

  (it "delete-guild-slash-command!"
    (sut/delete-guild-slash-command! 123 456)
    (let [[url] (stub/last-invocation-of :delete)]
      (should= "https://discord.com/api/v10/applications/app-id/guilds/123/commands/456" url)))

  (it "delete-global-slash-command!"
    (sut/delete-global-slash-command! 123)
    (let [[url] (stub/last-invocation-of :delete)]
      (should= "https://discord.com/api/v10/applications/app-id/commands/123" url)))

  )
