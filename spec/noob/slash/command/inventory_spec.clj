(ns noob.slash.command.inventory-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.slash.command.inventory]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper]
            [noob.style.core :as style]
            [noob.user :as user]
            [speclj.core :refer :all]))

(declare request)

(describe "Inventory"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (with request (spec-helper/->slash-request "inventory" @bill
                  :member {:user {:avatar      "bill-avatar"
                                  :global-name "Bill"}}))

  (context "display"

    (it "has no items"
      (slash/handle-command @request)
      (spec-helper/should-have-replied-ephemeral @request "Your inventory is empty."))

    (it "has one item"
      (db/tx (user/loot @bill @stick))
      (slash/handle-command @request)
      (spec-helper/should-have-replied @request
        [:<> [:button {:id (str "inventory-button-" (:id @stick)) :class "primary"} "Stick"]]
        :embed {:title       "Inventory"
                :description "Stick ⚔️ 1 ⭐️ 1"
                :color       style/green
                :author      (user/->author (:member @request))}))

    (it "has two items"
      (-> @bill
          (user/loot @stick)
          (user/loot @propeller-hat)
          db/tx)
      (slash/handle-command @request)
      (spec-helper/should-have-replied @request
        [:<>
         [:button {:id (str "inventory-button-" (:id @propeller-hat)) :class "primary"} "Propeller Hat"]
         [:button {:id (str "inventory-button-" (:id @stick)) :class "primary"} "Stick"]]
        :embed {:title       "Inventory"
                :description "Propeller Hat 👁 2 ⭐️ 2\nStick ⚔️ 1 ⭐️ 1"
                :color       style/green
                :author      (user/->author (:member @request))}))

    (it "has two items - one equipped"
      (-> @bill
          (user/loot @propeller-hat)
          (user/loot @stick)
          (user/equip @stick)
          db/tx)
      (slash/handle-command @request)
      (spec-helper/should-have-replied @request
        [:<>
         [:button {:id (str "inventory-button-" (:id @stick)) :class "success"} "Stick"]
         [:button {:id (str "inventory-button-" (:id @propeller-hat)) :class "primary"} "Propeller Hat"]]
        :embed {:title       "Inventory"
                :description "Stick ⚔️ 1 ⭐️ 1\nPropeller Hat 👁 2 ⭐️ 2"
                :color       style/green
                :author      (user/->author (:member @request))})))

  )
