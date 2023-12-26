(ns noob.slash.schema.full-spec
  (:require [c3kit.apron.corec :as ccc]
            [discord.option :as option]
            [noob.slash.schema.full :as sut]
            [speclj.core :refer :all]))


(defmacro should-have-command
  ([commands name description] `(should-have-command ~commands ~name ~description nil))
  ([commands name description options]
   `(let [command# (ccc/ffilter #(= ~name (:name %)) ~commands)]
      (should= ~description (:description command#))
      (should= ~options (:options command#))
      (should= 1 (:type command#)))))

(defmacro dev-should-have
  ([name description] `(should-have-command sut/dev-commands ~name ~description))
  ([name description options] `(should-have-command sut/dev-commands ~name ~description ~options)))

(defmacro global-should-have
  ([name description] `(should-have-command sut/prod-commands ~name ~description))
  ([name description options] `(should-have-command sut/prod-commands ~name ~description ~options)))


(describe "Full Slash Schema"

  (it "dev commands"
    (should= 0 (count sut/dev-commands)))

  (it "global commands"
    (should= 9 (count sut/prod-commands))
    (global-should-have "attack" "Attack another player!"
                        [(option/->user! "target" "The person you want to attack.")])
    (global-should-have "daily" "Redeem your daily Niblets!")
    (global-should-have "give" "Give some niblets to that special someone"
                        [(option/->user! "recipient" "The recipient of your handout")
                         (option/->int! "amount" "The number of niblets to bestow")])
    (global-should-have "inventory" "See your inventory!")
    (global-should-have "love" "Love another player ❤️"
                        [(option/->user! "beloved" "That special someone 🫶")])
    (global-should-have "shop" "Get in, loser. We're going shopping!")
    (global-should-have "stats" "View your player stats.")
    (global-should-have "steal" "Steal Niblets from another player!"
                        [(option/->user! "victim" "The person you will be stealing from.")])
    (global-should-have "weekly" "Redeem your weekly Niblets!"))

  )
