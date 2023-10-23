(ns noob.slash.give-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus]
            [noob.bogus :refer [bill ted]]
            [noob.slash.core :as slash]
            [noob.slash.give]
            [noob.spec-helper :as spec-helper :refer [should-have-replied should-have-replied-ephemeral]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(defn ->give-request [sender recipient amount]
  {:data   {:name    "give"
            :options {"recipient" (:discord-id recipient)
                      "amount"    amount}}
   :member {:user {:id (:discord-id sender)}}})

(describe "Give Command"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :user)

  (it "tries to /steal"
    (let [request (->give-request @bill @ted -1)]
      (slash/handle-name request)
      (should-have-replied-ephemeral request "Are you trying to /steal Niblets?")
      (should-be-nil (:niblets @bill))
      (should-be-nil (:niblets @ted))))

  (it "gives nothing"
    (let [request (->give-request @bill @ted 0)]
      (slash/handle-name request)
      (should-have-replied-ephemeral request "How many Niblets do you want to give?")
      (should-be-nil (:niblets @bill))
      (should-be-nil (:niblets @ted))))

  (it "has no niblets to give"
    (let [request (->give-request @bill @ted 1)]
      (slash/handle-name request)
      (should-have-replied-ephemeral request "You don't have enough Niblets. LOL")
      (should-be-nil (:niblets @bill))
      (should-be-nil (:niblets @ted))))

  (it "gives one niblet"
    (let [request (->give-request @bill @ted 1)]
      (db/tx @bill :niblets 1)
      (slash/handle-name request)
      (should-have-replied request "<@bill-id> gave <@ted-id> 1 Niblet!")
      (should= 0 (:niblets @bill))
      (should= 1 (:niblets @ted))))

  (it "gives two niblets"
    (let [request (->give-request @bill @ted 2)]
      (db/tx @bill :niblets 2)
      (slash/handle-name request)
      (should-have-replied request "<@bill-id> gave <@ted-id> 2 Niblets!")
      (should= 0 (:niblets @bill))
      (should= 2 (:niblets @ted))))

  (it "gives ten niblets"
    (let [request (->give-request @ted @bill 10)]
      (db/tx @ted :niblets 20)
      (slash/handle-name request)
      (should-have-replied request "<@ted-id> gave <@bill-id> 10 Niblets!")
      (should= 10 (:niblets @ted))
      (should= 10 (:niblets @bill))))

  (it "new user gives niblets"
    (let [request (->give-request {:discord-id "new-guy"} @bill 1)]
      (slash/handle-name request)
      (should-have-replied-ephemeral request "You don't have enough Niblets. LOL")
      (should-be-nil (user/by-discord-id "new-guy"))
      (should-be-nil (:niblets @bill))))

  (it "gives niblets to new user"
    (let [request (->give-request @bill {:discord-id "new-guy"} 10)]
      (db/tx @bill :niblets 12)
      (slash/handle-name request)
      (should-have-replied request "<@bill-id> gave <@new-guy> 10 Niblets!")
      (should= {:kind :user :niblets 10 :discord-id "new-guy"} (dissoc (user/by-discord-id "new-guy") :id))
      (should= 2 (:niblets @bill))))

  )
