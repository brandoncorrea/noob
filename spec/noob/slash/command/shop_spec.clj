(ns noob.slash.command.shop-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.slash.command.shop :as sut]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-replied]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(declare request)
(declare products)

(describe "Shop"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (context "shop menu"
    (it "head with perception"
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250 👁 2"]]
               (sut/->shop-menu [@propeller-hat])))

    (it "main hand with attack"
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description "A sticky stick." :value (:id @stick)} "Stick – Main Hand – 🪙 100 ⚔️ 1"]]
               (sut/->shop-menu [@stick])))

    (it "two items"
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250 👁 2"]
                [:option {:description "A sticky stick." :value (:id @stick)} "Stick – Main Hand – 🪙 100 ⚔️ 1"]]
               (sut/->shop-menu [@propeller-hat @stick])))

    (it "sneak"
      (db/tx @propeller-hat :sneak 4 :perception nil)
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250 🥷 4"]]
               (sut/->shop-menu [@propeller-hat])))

    (it "defense"
      (db/tx @propeller-hat :defense 3 :perception nil)
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250 🛡 3"]]
               (sut/->shop-menu [@propeller-hat])))

    (it "level 0 attributes"
      (db/tx @propeller-hat :attack 0 :defense 0 :sneak 0 :perception 0)
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250"]]
               (sut/->shop-menu [@propeller-hat])))

    (it "negative attributes"
      (db/tx @propeller-hat :perception -1)
      (should= [:select#shop-menu {:placeholder "Select an option"}
                [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat – Head – 🪙 250 👁 -1"]]
               (sut/->shop-menu [@propeller-hat])))
    )

  (context "show menu"
    (with products (db/find :product))
    (with request (spec-helper/->slash-request "shop" @bill))

    (it "no inventory"
      (run! db/delete @products)
      (slash/handle-command @request)
      (should-have-replied @request "There are no items available to purchase."))

    (it "user owns all inventory"
      (db/tx (reduce user/loot @bill @products))
      (slash/handle-command @request)
      (should-have-replied @request "There are no items available to purchase."))

    (it "excludes items above user's current level"
      (db/tx @stick :level 3)
      (slash/handle-command @request)
      (should-have-replied @request (sut/->shop-menu [@propeller-hat])))

    (it "user owns one item"
      (db/tx (user/loot @bill @stick))
      (slash/handle-command @request)
      (should-have-replied @request (sut/->shop-menu [@propeller-hat])))

    (it "user owns nothing"
      (slash/handle-command @request)
      (should-have-replied @request (sut/->shop-menu [@stick @propeller-hat])))

    )
  )
