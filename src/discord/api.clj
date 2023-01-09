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

(defn post! [uri body]
  (http/post (str root uri) (authorize {:form-params body :content-type :json})))

(defn get! [uri]
  (http/get (str root uri) (authorize {:as :json})))

(defn reply-interaction! [{:keys [id token]} content]
  (when (and id token content)
    (post! (str "/interactions/" id "/" token "/callback")
           {:type 4 :data {:content content}})))

(defn create-guild-slash-command!
  ([guild name] (create-guild-slash-command! guild name nil))
  ([guild name description] (create-guild-slash-command! guild name description nil))
  ([guild name description options]
   (when name
     (post! (str "/applications/" config/app-id (when guild "/guilds/") guild "/commands")
            (ccc/remove-nils
              {:name        name
               :type        1
               :description description
               :options     options})))))

(def create-global-slash-command! (partial create-guild-slash-command! nil))

(defn guild-preview [guild-id]
  (get! (str "/guilds/" guild-id "/preview")))
