(ns noob.slash.action.inventory-button-spec
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.slash.action.inventory-button]
            [noob.slash.command.inventory :as inventory]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-replied-ephemeral should-have-updated-message]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(declare request)

(defmacro should-update-inventory [request user]
  `(let [request# ~request
         [content# options#] (inventory/->inventory-content request# ~user)]
     (should-have-updated-message request# content# options#)))

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
    (should-update-inventory @request @bill))

  (it "unequips an equipped item"
    (user/loot! @bill @stick)
    (user/equip! @bill @stick)
    (slash/handle-action @request)
    (should= [] (user/loadout @bill))
    (should-update-inventory @request @bill))

  (it "equips an item to a slot that already has an item equipped"
    (db/tx @stick :slot :head)
    (user/loot! @bill @stick)
    (user/loot! @bill @propeller-hat)
    (user/equip! @bill @propeller-hat)
    (slash/handle-action @request)
    (should= [(:id @stick)] (user/loadout @bill))
    (should-update-inventory @request @bill))

  ;; Anything sent to utilc/->edn must be realized
  (it "does not store standard output"
    (user/loot! @bill @stick)
    (user/equip! @bill @propeller-hat)
    (with-redefs [db/entity (comp ccc/->inspect db/entity)]
      (slash/handle-action @request)
      (should= [(:id @stick) (:id @propeller-hat)] (user/loadout @bill))))

  )
