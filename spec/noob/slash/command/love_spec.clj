(ns noob.slash.command.love-spec
  (:require [noob.bogus :as bogus]
            [noob.bogus :refer [bill ted]]
            [noob.slash.command.love :as sut]
            [noob.slash.core :as slash]
            [noob.spec-helper :as spec-helper :refer [should-have-replied]]
            [speclj.core :refer :all]))

(def request)

(describe "Love"
  (with-stubs)
  (spec-helper/stub-discord)
  (bogus/with-kinds :user)

  (context "loving others"

    (with request {:data   {:name     "love"
                            :resolved {:members {(:discord-id @ted) {:nick "teddy"}}}
                            :options  {:beloved (:discord-id @ted)}}
                   :member {:nick "billy"
                            :user {:id (:discord-id @bill)}}})

    (redefs-around [rand-nth            (stub :rand-nth {:invoke rand-nth})
                    sut/others-messages ["%1$s loves %2$s"]
                    sut/self-messages   ["%s loves themselves"]])

    (it "loves another"
      (slash/handle-command @request)
      (should-have-replied @request "billy loves teddy")
      (should-have-invoked :rand-nth {:with [sut/others-messages]}))

    (it "loves self"
      (let [request (assoc-in @request [:data :options :beloved] (:discord-id @bill))]
        (slash/handle-command request)
        (should-have-replied request "billy loves themselves")
        (should-have-invoked :rand-nth {:with [sut/self-messages]})))
    )
  )
