(ns noob.slash.command.help-spec
  (:require [noob.slash.command.help :as sut]
            [noob.slash.command.schema.full :as command-schema]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-embedded]]
            [noob.style.core :as style]
            [speclj.core :refer :all]))

(describe "Help Command"
  (with-stubs)
  (spec-helper/stub-discord)

  (it "responds with a help message"
    (let [request {:data {:name "help"}}
          embed   {:title       "Help!"
                   :description (sut/commands->help-message command-schema/prod-commands)
                   :color       style/green}]
      (slash/handle-command request)
      (should-have-embedded request embed)))

  (context "commands->help-message"

    (it "no commands"
      (should= "LOL NOOB there's nothing to show here."
               (sut/commands->help-message [])))

    (it "one command"
      (should= "/first\nThis is the first command."
               (sut/commands->help-message [{:name "first" :description "This is the first command."}])))

    (it "two commands"
      (should= "/first\nThis is the first command.\n\n/second\nAnother command."
               (sut/commands->help-message [{:name "first" :description "This is the first command."}
                                            {:name "second" :description "Another command."}])))
    )

  )
