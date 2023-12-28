(ns noob.slash.action.inventory-button-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.slash.action.inventory-button]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-replied-ephemeral]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(declare request)

(describe "Inventory Button"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (with request {:data   {:custom-id (str "inventory-button-" (:id @stick))}
                 :member {:user {:id (:discord-id @bill)}}})

  (it "does not own the product"
    (slash/handle-action @request)
    (should-have-replied-ephemeral @request "You do not own this item."))

  (it "item does not exist"
    (let [request @request]
      (user/loot! @bill @stick)
      (db/delete @stick)
      (slash/handle-action request)
      (should-have-replied-ephemeral request "This item does not exist.")))

  (it "equips an unequipped item"
    (user/loot! @bill @stick)
    (slash/handle-action @request)
    (should= [(:id @stick)] (user/loadout @bill))
    (should-have-replied-ephemeral @request "Item equipped!"))

  (it "unequips an equipped item"
    (user/loot! @bill @stick)
    (user/equip! @bill @stick)
    (slash/handle-action @request)
    (should= [] (user/loadout @bill))
    (should-have-replied-ephemeral @request "Item unequipped!"))

  (it "equips an item to a slot that already has an item equipped"
    (db/tx @stick :slot :head)
    (user/loot! @bill @stick)
    (user/loot! @bill @propeller-hat)
    (user/equip! @bill @propeller-hat)
    (slash/handle-action @request)
    (should= [(:id @stick)] (user/loadout @bill))
    (should-have-replied-ephemeral @request "Item equipped!"))

  )
