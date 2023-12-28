(ns noob.slash.command.steal-spec
  (:require [c3kit.bucket.api :as db]
            [noob.bogus :as bogus :refer [bill propeller-hat stick ted]]
            [noob.roll :as roll]
            [noob.slash.command.steal :as sut]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-created-message should-have-replied should-have-replied-ephemeral]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(def request)

(describe "Steal"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (redefs-around [sut/fail-messages           ["%1$s fails to steal from %2$s"]
                  sut/self-messages           ["%s steals from themselves"]
                  sut/almost-success-messages ["%s almost succeeded"]])

  (context "stealing niblets"

    (with request {:data   {:name "steal" :options {:victim (:discord-id @ted)}}
                   :member {:nick "bill"
                            :user {:id (:discord-id @bill)}}})

    (it "steals from self"
      (let [request (assoc-in @request [:data :options :victim] (:discord-id @bill))]
        (slash/handle-command request)
        (should-have-replied request (str (user/mention @bill) " steals from themselves"))))

    (it "steals from user with no Niblets"
      (with-redefs [roll/steal? (constantly true)]
        (slash/handle-command @request)
        (should-have-replied-ephemeral @request "There are no Niblets to steal :(")))

    (it "steals niblets from a user"
      (with-redefs [roll/steal?         (constantly true)
                    roll/stolen-niblets (stub :stolen-niblets {:return 10})]
        (db/tx @ted :niblets 100)
        (slash/handle-command @request)
        (should-have-replied-ephemeral @request "You stole 10 Niblets 😈")
        (should-have-invoked :stolen-niblets {:with [2 0 1 0]})
        (should= 90 (:niblets @ted))
        (should= 10 (:niblets @bill))))

    (it "cannot steal more than what the user has"
      (with-redefs [roll/steal?         (constantly true)
                    roll/stolen-niblets (stub :stolen-niblets {:return 10})]
        (db/tx @ted :niblets 1)
        (slash/handle-command @request)
        (should-have-replied-ephemeral @request "You stole 1 Niblets 😈")
        (should= 0 (:niblets @ted))
        (should= 1 (:niblets @bill))))

    (it "cannot 'give' niblets when calculation returns non-positive"
      (with-redefs [roll/steal?         (constantly true)
                    roll/stolen-niblets (stub :stolen-niblets {:return -10})]
        (db/tx @ted :niblets 100)
        (slash/handle-command @request)
        (should-have-replied @request "<@bill-id> almost succeeded")
        (should= 100 (:niblets @ted))
        (should-be-nil (:niblets @bill))))

    (it "factors in sneak and perception when attempting to steal"
      (db/tx @stick :sneak 5)
      (db/tx (user/equip @bill @stick))
      (db/tx (user/equip @ted @propeller-hat))
      (with-redefs [roll/steal?         (stub :steal? {:return true})
                    roll/stolen-niblets (stub :stolen-niblets {:return 10})]
        (db/tx @ted :niblets 100)
        (slash/handle-command @request)
        (should-have-invoked :steal? {:with [2 5 1 2]})
        (should-have-invoked :stolen-niblets {:with [2 5 1 2]})))

    (it "fails to steal from another user"
      (with-redefs [roll/steal?         (constantly false)
                    roll/stolen-niblets (stub :stolen-niblets {:return 10})]
        (db/tx @ted :niblets 100)
        (slash/handle-command @request)
        (should-have-replied @request (str (user/mention @bill) " fails to steal from " (user/mention @ted)))
        (should= 100 (:niblets @ted))
        (should-be-nil (:niblets @bill))))

    (it "thief pays a fee when they are caught"
      (with-redefs [roll/steal?         (constantly false)
                    roll/stolen-niblets (stub :stolen-niblets {:return 10})]
        (db/tx @ted :niblets 100)
        (db/tx @bill :niblets 100)
        (slash/handle-command @request)
        (should-have-invoked :stolen-niblets {:with [2 0 1 0]})
        (should-have-replied @request (str (user/mention @bill) " fails to steal from " (user/mention @ted)))
        (should-have-created-message @request (str (user/mention @bill) " pays a 30 Niblet fine!"))
        (should= 130 (:niblets @ted))
        (should= 70 (:niblets @bill))))

    (it "a caught thief does not pay a negative fee"
      (with-redefs [roll/steal?         (constantly false)
                    roll/stolen-niblets (stub :stolen-niblets {:return -10})]
        (db/tx @ted :niblets 100)
        (db/tx @bill :niblets 100)
        (slash/handle-command @request)
        (should-have-replied @request (str (user/mention @bill) " fails to steal from " (user/mention @ted)))
        (should-not-have-invoked :discord/create-message!)
        (should= 100 (:niblets @ted))
        (should= 100 (:niblets @bill))))

    )

  )
