(ns noob.slash.shop-spec
  (:require [c3kit.bucket.db :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick ted]]
            [noob.slash.core :as slash]
            [noob.slash.shop :as sut]
            [noob.spec-helper :as spec-helper :refer [should-have-edited-message should-have-replied should-have-replied-ephemeral]]
            [speclj.core :refer :all]))

(defn ->request [{:keys [discord-id]} username]
  {:data   {:name "shop"}
   :member {:user {:id discord-id :username username}}})

(def request)
(def products)

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
    (with products (db/find-all :product :name))
    (with request {:data   {:name "shop"}
                   :member {:user {:id (:discord-id @bill) :username "Bill"}}})

    (it "no inventory"
      (run! db/retract @products)
      (slash/handle-name @request)
      (should-have-replied @request "There are no items available to purchase."))

    (it "user owns all inventory"
      (db/tx @bill :inventory (map :id @products))
      (slash/handle-name @request)
      (should-have-replied @request "There are no items available to purchase."))

    (it "excludes items above user's current level"
      (db/tx @stick :level 3)
      (slash/handle-name @request)
      (should-have-replied @request (sut/->shop-menu [@propeller-hat])))

    (it "user owns one item"
      (db/tx @bill :inventory [(:id @stick)])
      (slash/handle-name @request)
      (should-have-replied @request (sut/->shop-menu [@propeller-hat])))

    (it "user owns nothing"
      (slash/handle-name @request)
      (should-have-replied @request (sut/->shop-menu [@propeller-hat @stick])))

    )

  (context "select item"
    (with request {:data   {:values    [(str (:id @stick))]
                            :custom-id "shop-menu"}
                   :member {:user {:id (:discord-id @bill) :username "Bill"}}})

    (it "missing values"
      (let [request (update @request :data dissoc :values)]
        (slash/handle-custom-id request)
        (should-have-replied-ephemeral request "That item doesn't seem to exist.")))

    (it "item does not exist"
      (db/retract @stick)
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "That item doesn't seem to exist."))

    (it "id is not a product"
      (let [request (assoc-in @request [:data :values] [(str (:id @ted))])]
        (slash/handle-custom-id request)
        (should-have-replied-ephemeral request "That item doesn't seem to exist.")))

    (it "user does not exist"
      (db/tx @propeller-hat :price 0 :level 1)
      (let [request (assoc-in @request [:data :values] [(str (:id @propeller-hat))])
            bill-id (:discord-id @bill)]
        (db/retract @bill)
        (slash/handle-custom-id request)
        (should-have-replied-ephemeral request "Propeller Hat has been added to your inventory!")
        (let [{:keys [inventory loadout niblets]} (db/ffind-by :user :discord-id bill-id)]
          (should-contain (:id @propeller-hat) inventory)
          (should-contain (:id @propeller-hat) loadout)
          (should= 0 niblets))))

    (it "inadequate niblets"
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "You do not have enough Niblets to purchase this item.")
      (should-not-contain (:id @stick) (:inventory @bill)))

    (it "inadequate level"
      (db/tx @bill :xp 0 :niblets 500)
      (let [request (assoc-in @request [:data :values] [(str (:id @propeller-hat))])]
        (slash/handle-custom-id request)
        (should-have-replied-ephemeral request "You need to be at least level 2 to purchase this item.")))

    (it "already owns item"
      (db/tx @bill :inventory #{(:id @stick)})
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "It looks like you already own a Stick."))

    (it "purchases item"
      (db/tx @bill :xp 1000 :niblets 500 :loadout #{(:id @propeller-hat)})
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
      (should-contain (:id @stick) (:inventory @bill))
      (should= 400 (:niblets @bill))
      (should-contain (:id @stick) (:loadout @bill)))

    (it "retains currently equipped items over newly purchased items"
      (db/tx @propeller-hat :slot :main-hand)
      (db/tx @bill :xp 1000 :niblets 500 :loadout #{(:id @propeller-hat)})
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
      (should-contain (:id @stick) (:inventory @bill))
      (should= 400 (:niblets @bill))
      (should-contain (:id @propeller-hat) (:loadout @bill))
      (should-not-contain (:id @stick) (:loadout @bill)))

    (it "removes item from parent message"
      (db/tx @bill :niblets 500)
      (slash/handle-custom-id @request)
      (should-have-replied-ephemeral @request "Stick has been added to your inventory!")
      (should-have-edited-message @request (sut/->shop-menu [@propeller-hat])))
    )
  )
