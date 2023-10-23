(ns noob.slash.attack-spec
  (:require [c3kit.bucket.db :as db]
            [noob.bogus :as bogus :refer [bill ted]]
            [noob.slash.attack :as sut]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-created-message should-have-replied]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(def request)

(describe "Attack"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :all)

  (redefs-around [sut/success-messages ["%1$s attacks %2$s"]
                  sut/fail-messages    ["%1$s fails to attack %2$s"]
                  sut/self-messages    ["%s attacks themselves"]])

  (context "attacking people"

    (with request {:data   {:name "attack" :options {"target" (:discord-id @ted)}}
                   :member {:user {:id (:discord-id @bill)}}})

    (it "attacks self"
      (let [request (assoc-in @request [:data :options "target"] (:discord-id @bill))]
        (slash/handle-name request)
        (should-have-replied request (str (user/mention @bill) " attacks themselves"))))

    (it "fails attack"
      (with-redefs [user/roll (fn [_user ability] (if (= :defense ability) 1 0))]
        (slash/handle-name @request)
        (should-have-replied @request (str (user/mention @bill) " fails to attack " (user/mention @ted)))
        (should= 100 (:xp @bill))
        (should= (user/xp-reward @ted 15 2) (:xp @ted))))

    (it "succeeds attack"
      (with-redefs [user/roll (fn [_user ability] (if (= :attack ability) 1 0))]
        (slash/handle-name @request)
        (should-have-replied @request (str (user/mention @bill) " attacks " (user/mention @ted)))
        (should= (+ 100 (user/xp-reward @bill 25 1)) (:xp @bill))
        (should-be-nil (:xp @ted))))

    (it "winner levels up"
      (with-redefs [user/roll (fn [_user ability] (if (= :attack ability) 1 0))]
        (db/tx @bill :xp 99)
        (slash/handle-name @request)
        (should-have-created-message @request (str (user/mention @bill) " has reached level 2!"))))

    (it "target wins on ties"
      (with-redefs [user/roll (fn [_user ability] (if (= :attack ability) 1 0.75))]
        (slash/handle-name @request)
        (should-have-replied @request (str (user/mention @bill) " fails to attack " (user/mention @ted)))))
    )
  )
