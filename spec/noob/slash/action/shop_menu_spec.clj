(ns noob.slash.action.shop-menu-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus]
            [noob.bogus :refer [bill propeller-hat stick ted]]
            [noob.slash.command.shop :as shop]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-edited-message should-have-replied-ephemeral]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(declare request)

(describe "Shop Menu Action"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (with request (spec-helper/->slash-request "shop" @bill
                  :data {:values    [(str (:id @stick))]
                         :custom-id "shop-menu"}))

  (it "missing values"
    (let [request (update @request :data dissoc :values)]
      (slash/handle-action request)
      (should-have-replied-ephemeral request "That item doesn't seem to exist.")))

  (it "item does not exist"
    (db/delete @stick)
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "That item doesn't seem to exist."))

  (it "id is not a product"
    (let [request (assoc-in @request [:data :values] [(str (:id @ted))])]
      (slash/handle-action request)
      (should-have-replied-ephemeral request "That item doesn't seem to exist.")))

  (it "user does not exist"
    (db/tx @propeller-hat :price 0 :level 1)
    (let [request (assoc-in @request [:data :values] [(str (:id @propeller-hat))])
          bill-id (:discord-id @bill)]
      (db/delete @bill)
      (slash/handle-action request)
      (should-have-replied-ephemeral request "Propeller Hat has been added to your inventory!")
      (let [user (db/ffind-by :user :discord-id bill-id)]
        (should-contain (:id @propeller-hat) (user/inventory user))
        (should-contain (:id @propeller-hat) (user/loadout user))
        (should= 0 (:niblets user)))))

  (it "inadequate niblets"
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "You do not have enough Niblets to purchase this item.")
    (should-not-contain (:id @stick) (user/inventory @bill)))

  (it "inadequate level"
    (db/tx @bill :xp 0 :niblets 500)
    (let [request (assoc-in @request [:data :values] [(str (:id @propeller-hat))])]
      (slash/handle-action request)
      (should-have-replied-ephemeral request "You need to be at least level 2 to purchase this item.")))

  (it "already owns item"
    (db/tx (user/loot @bill @stick))
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "It looks like you already own a Stick."))

  (it "purchases item"
    (-> (assoc @bill :xp 1000 :niblets 500)
        (user/equip @propeller-hat)
        db/tx)
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
    (should-contain (:id @stick) (user/inventory @bill))
    (should= 400 (:niblets @bill))
    (should-contain (:id @stick) (user/loadout @bill)))

  (it "retains currently equipped items over newly purchased items"
    (db/tx @propeller-hat :slot :main-hand)
    (-> (assoc @bill :xp 1000 :niblets 500)
        (user/equip @propeller-hat)
        db/tx)
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
    (should-contain (:id @stick) (user/inventory @bill))
    (should= 400 (:niblets @bill))
    (should-contain (:id @propeller-hat) (user/loadout @bill))
    (should-not-contain (:id @stick) (user/loadout @bill)))

  (it "removes item from parent message"
    (db/tx @bill :niblets 500)
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
    (should-have-edited-message @request (shop/->shop-menu [@propeller-hat])))
  )
