(ns noob.slash.shop-spec
  (:require [c3kit.bucket.db :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick]]
            [noob.slash.core :as slash]
            [noob.slash.shop]
            [noob.spec-helper :as spec-helper :refer [should-have-replied-ephemeral]]
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

  (context "show menu"
    (with products (db/find-all :product :name))
    (with request {:data   {:name "shop"}
                   :member {:user {:id (:discord-id @bill) :username "Bill"}}})

    (it "no inventory"
      (run! db/retract @products)
      (slash/handle-slash @request)
      (should-have-replied-ephemeral @request "There are no items available to purchase."))

    (it "user owns all inventory"
      (db/tx @bill :inventory (map :id @products))
      (slash/handle-slash @request)
      (should-have-replied-ephemeral @request "There are no items available to purchase."))

    (it "user owns one item"
      (db/tx @bill :inventory [(:id @stick)])
      (slash/handle-slash @request)
      (should-have-replied-ephemeral @request
        [:select#shop-menu {:placeholder "Select an option"}
         [:option {:description nil :value (:id @propeller-hat)} "Propeller Hat"]]))

    (it "user owns nothing"
      (slash/handle-slash @request)
      (should-have-replied-ephemeral @request
        [:select#shop-menu {:placeholder "Select an option"}
         [:option {:description nil
                   :value       (:id @propeller-hat)}
          "Propeller Hat"]
         [:option {:description "A sticky stick."
                   :value       (:id @stick)}
          "Stick"]]))

    )

  (context "select item"
    (with request {:data   {:custom-id "shop-menu"}
                   :member {:user {:id (:discord-id @bill) :username "Bill"}}})

    (it "responds with a message"
      (slash/handle-slash @request)
      (should-have-replied-ephemeral @request "This is currently read-only!"))
    )
  )
