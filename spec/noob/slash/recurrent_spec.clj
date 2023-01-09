(ns noob.slash.recurrent-spec
  (:require [c3kit.apron.time :as time]
            [c3kit.bucket.db :as db]
            [noob.bogus :as bogus :refer [bill]]
            [noob.command :as command]
            [noob.slash.core :as slash]
            [noob.slash.recurrent]
            [noob.spec-helper :as spec-helper]
            [speclj.core :refer :all]))

(defn ->recurrent-request [name {:keys [discord-id]} username]
  {:data   {:name name}
   :member {:user {:id discord-id :username username}}})
(def ->daily-request (partial ->recurrent-request "daily"))
(def ->weekly-request (partial ->recurrent-request "weekly"))

(def now (time/now))

(describe "Recurrent Command"
  (with-stubs)
  (spec-helper/stub-discord)
  (spec-helper/stub-now now)
  (bogus/with-kinds :user)
  (before (bogus/init! :user))

  (context "daily"
    (it "one hour until next execution"
      (command/create-daily-command! @bill (-> now (time/before (time/days 1)) (time/after (time/hours 1))))
      (let [request (->daily-request @bill "Bill")]
        (slash/handle-slash request)
        (should-have-invoked :discord/reply-interaction! {:with [request "Your daily reward will be ready in 1 hour!"]})))

    (it "three hours twelve minutes until next execution"
      (command/create-daily-command! @bill (-> now (time/before (time/days 1)) (time/after (time/hours 3)) (time/after (time/minutes 12))))
      (let [request (->daily-request @bill "Bill")]
        (slash/handle-slash request)
        (should-have-invoked :discord/reply-interaction! {:with [request "Your daily reward will be ready in 3 hours and 12 minutes!"]})))

    (it "succeeds with missing user"
      (let [request (->daily-request {:discord-id "napoleon-id"} "Napoleon")]
        (slash/handle-slash request)
        (let [{:keys [id niblets]} (db/ffind-by :user :discord-id "napoleon-id")]
          (should (pos? niblets))
          (should-have-invoked :discord/reply-interaction! {:with [request (str "Napoleon received " niblets " Niblets!")]})
          (should= now (:last-ran-at (db/ffind-by :command :user id :interval :daily))))))

    (it "succeeds with missing command"
      (let [request (->daily-request @bill "Bill")]
        (slash/handle-slash request)
        (should (pos? (:niblets @bill)))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :daily)))))

    (it "succeeds with existing command"
      (let [request (->daily-request @bill "Bill")]
        (command/create-daily-command! @bill (time/before now (time/hours 25)))
        (slash/handle-slash request)
        (should (pos? (:niblets @bill)))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= 1 (db/count-by :command :user (:id @bill) :interval :daily))
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :daily)))))

    (it "succeeds when user has niblets"
      (db/tx @bill :niblets 100)
      (let [request (->daily-request @bill "Bill")]
        (slash/handle-slash request)
        (should (> (:niblets @bill) 100))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= 1 (db/count-by :command :user (:id @bill) :interval :daily))
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :daily)))))
    )

  (context "weekly"
    (it "one day until next execution"
      (command/create-weekly-command! @bill (time/before now (time/days 6)))
      (let [request (->weekly-request @bill "Bill")]
        (slash/handle-slash request)
        (should-have-invoked :discord/reply-interaction! {:with [request "Your weekly reward will be ready in 1 day!"]})))

    (it "3 days 12 hours and 4 minutes until next execution"
      (command/create-weekly-command! @bill
                                      (-> now
                                          (time/before (time/days 7))
                                          (time/after (time/days 3))
                                          (time/after (time/hours 12))
                                          (time/after (time/minutes 4))))
      (let [request (->weekly-request @bill "Bill")]
        (slash/handle-slash request)
        (should-have-invoked :discord/reply-interaction! {:with [request "Your weekly reward will be ready in 3 days, 12 hours, and 4 minutes!"]})))

    (it "succeeds with missing user"
      (let [request (->weekly-request {:discord-id "napoleon-id"} "Napoleon")]
        (slash/handle-slash request)
        (let [{:keys [id niblets]} (db/ffind-by :user :discord-id "napoleon-id")]
          (should (pos? niblets))
          (should-have-invoked :discord/reply-interaction! {:with [request (str "Napoleon received " niblets " Niblets!")]})
          (should= now (:last-ran-at (db/ffind-by :command :user id :interval :weekly))))))

    (it "succeeds with missing command"
      (let [request (->weekly-request @bill "Bill")]
        (slash/handle-slash request)
        (should (pos? (:niblets @bill)))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :weekly)))))

    (it "succeeds with existing command"
      (let [request (->weekly-request @bill "Bill")]
        (command/create-weekly-command! @bill (time/before now (time/days 8)))
        (slash/handle-slash request)
        (should (pos? (:niblets @bill)))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= 1 (db/count-by :command :user (:id @bill) :interval :weekly))
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :weekly)))))

    (it "succeeds when user has niblets"
      (db/tx @bill :niblets 500)
      (let [request (->weekly-request @bill "Bill")]
        (slash/handle-slash request)
        (should (> (:niblets @bill) 550))
        (should-have-invoked :discord/reply-interaction! {:with [request (str "Bill received " (:niblets @bill) " Niblets!")]})
        (should= 1 (db/count-by :command :user (:id @bill) :interval :weekly))
        (should= now (:last-ran-at (db/ffind-by :command :user (:id @bill) :interval :weekly)))))
    )
  )
