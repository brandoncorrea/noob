(ns discord.api
  (:require [c3kit.apron.corec :as ccc]
            [clj-http.client :as http]
            [noob.config :as config]))

(def root "https://discord.com/api/v10")

(comment
  (def application-command-types
    {:chat-input 1
     :user       2
     :message    3})

  (def application-command-option-types
    {:sub-command       1
     :sub-command-group 2
     :string            3
     :integer           4
     :boolean           5
     :user              6
     :channel           7
     :role              8
     :mentionable       9
     :number            10
     :attachment        11}))

(defn authorize [request]
  (assoc-in request [:headers "Authorization"] (str "Bot " config/token)))

(defn- http-json [http-fn uri body]
  (http-fn (str root uri) (authorize {:form-params body :content-type :json})))

(defn post! [uri body] (http-json http/post uri body))
(defn patch! [uri body] (http-json http/patch uri body))
(defn get! [uri] (http/get (str root uri) (authorize {:as :json})))
(defn delete! [uri] (http/delete (str root uri) (authorize nil)))

(defn create-guild-slash-command! [guild {:keys [name description options]}]
  (when name
    (post! (str "/applications/" config/app-id (when guild "/guilds/") guild "/commands")
           (ccc/remove-nils
             {:name        name
              :type        1
              :description description
              :options     options}))))

(defn delete-guild-slash-command! [guild-id command-id]
  (delete! (str "/applications/" config/app-id "/guilds/" guild-id "/commands/" command-id)))

(defn delete-global-slash-command! [command-id]
  (delete! (str "/applications/" config/app-id "/commands/" command-id)))

(defn create-global-slash-command! [command]
  (create-guild-slash-command! nil command))

(defn guild-preview [guild-id]
  (get! (str "/guilds/" guild-id "/preview")))

(defn- get-body-or-empty [url]
  (let [res (get! url)]
    (if (= 200 (:status res))
      (:body res)
      [])))

(defn get-guild-commands [guild-id]
  (get-body-or-empty (str "/applications/" config/app-id "/guilds/" guild-id "/commands")))

(defn get-global-commands []
  (get-body-or-empty (str "/applications/" config/app-id "/commands")))
