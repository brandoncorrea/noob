(ns noob.slash.command.love-spec
  (:require [noob.bogus :as bogus]
            [noob.bogus :refer [bill ted]]
            [noob.slash.core :as slash]
            [noob.slash.command.love :as sut]
            [noob.spec-helper :as spec-helper :refer [should-have-replied]]
            [noob.user :as user]
            [speclj.core :refer :all]))

(def request)

(describe "Love"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :user)

  (context "loving others"

    (with request {:data   {:name "love" :options {"beloved" (:discord-id @ted)}}
                   :member {:user {:id (:discord-id @bill)}}})

    (redefs-around [rand-nth (stub :rand-nth {:invoke rand-nth})])

    (it "loves another"
      (with-redefs [sut/others-messages ["%1$s loves %2$s"]]
        (slash/handle-command @request)
        (should-have-replied @request (str (user/mention @bill) " loves " (user/mention @ted)))
        (should-have-invoked :rand-nth {:with [sut/others-messages]})))

    (it "loves self"
      (with-redefs [sut/self-messages ["%s loves themselves"]]
        (let [request (assoc-in @request [:data :options "beloved"] (:discord-id @bill))]
          (slash/handle-command request)
          (should-have-replied request (str (user/mention @bill) " loves themselves"))
          (should-have-invoked :rand-nth {:with [sut/self-messages]}))))

    )

  )
