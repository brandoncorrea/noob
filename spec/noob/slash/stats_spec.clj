(ns noob.slash.stats-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.core :as core]
            [noob.slash.core :as slash]
            [noob.slash.stats]
            [noob.spec-helper :as spec-helper :refer [should-have-embedded]]
            [noob.style.core :as style]
            [noob.user :as user]
            [speclj.core :refer :all]))

(def request)

(describe "Stats"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (context "displaying stats"

    (with request {:data   {:name "stats"}
                   :member {:user {:id          (:discord-id @bill)
                                   :avatar      "bill-avatar"
                                   :global-name "Bill"}}})

    (it "user does not exist"
      (db/delete @bill)
      (slash/handle-name @request)
      (should-have-embedded @request
        {:title       "Stats"
         :description (core/join-lines
                        "Niblets: 0"
                        "Level: 1"
                        "Experience: 0"
                        "⚔️ Attack: 0"
                        "🛡 Defense: 0"
                        "🥷 Stealth: 0"
                        "👁 Perception: 0")
         :color       style/green
         :author      {:name     (user/display-name (:member @request))
                       :icon_url (user/avatar (:member @request))}}))

    (it "User with no loadout"
      (slash/handle-name @request)
      (should-have-embedded @request
        {:title       "Stats"
         :description (core/join-lines
                        "Niblets: 0"
                        "Level: 2"
                        "Experience: 100"
                        "⚔️ Attack: 0"
                        "🛡 Defense: 0"
                        "🥷 Stealth: 0"
                        "👁 Perception: 0")
         :color       style/green
         :author      {:name     (user/display-name (:member @request))
                       :icon_url (user/avatar (:member @request))}}))

    (it "User with loadout"
      (db/tx @stick :sneak 3)
      (db/tx @propeller-hat :defense 1 :sneak 1)
      (db/tx @bill :loadout #{(:id @stick) (:id @propeller-hat)})
      (slash/handle-name @request)
      (should-have-embedded @request
        {:title       "Stats"
         :description (core/join-lines
                        "Niblets: 0"
                        "Level: 2"
                        "Experience: 100"
                        "⚔️ Attack: 1"
                        "🛡 Defense: 1"
                        "🥷 Stealth: 4"
                        "👁 Perception: 2")
         :color       style/green
         :author      {:name     (user/display-name (:member @request))
                       :icon_url (user/avatar (:member @request))}}))

    (it "with niblets"
      (db/tx @bill :niblets 12)
      (slash/handle-name @request)
      (should-have-embedded @request
        {:title       "Stats"
         :description (core/join-lines
                        "Niblets: 12"
                        "Level: 2"
                        "Experience: 100"
                        "⚔️ Attack: 0"
                        "🛡 Defense: 0"
                        "🥷 Stealth: 0"
                        "👁 Perception: 0")
         :color       style/green
         :author      {:name     (user/display-name (:member @request))
                       :icon_url (user/avatar (:member @request))}}))

    (it "with xp"
      (db/tx @bill :xp 400)
      (slash/handle-name @request)
      (should-have-embedded @request
        {:title       "Stats"
         :description (core/join-lines
                        "Niblets: 0"
                        "Level: 3"
                        "Experience: 400"
                        "⚔️ Attack: 0"
                        "🛡 Defense: 0"
                        "🥷 Stealth: 0"
                        "👁 Perception: 0")
         :color       style/green
         :author      {:name     (user/display-name (:member @request))
                       :icon_url (user/avatar (:member @request))}}))

    )
  )