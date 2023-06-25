(ns discord.components.hiccup
  (:require [c3kit.apron.corec :as ccc]
            [clojure.string :as str]
            [discord.components.component :as component]))

(defn re-join-keys [re keys tag options]
  (->> (re-seq re (name tag))
       (map #(subs % 1))
       (concat (map options keys))
       (str/join " ")
       str/trim))

(defn- join-keys [re [primary-key :as keys] tag options]
  (let [val (re-join-keys re keys tag options)]
    (cond-> (apply dissoc options keys)
            (ccc/not-blank? val) (assoc primary-key val))))

(def join-ids (partial join-keys #"#[^\.#]*" [:custom_id :custom-id :id]))
(def join-classes (partial join-keys #"\.[^\.#]*" [:class]))
(defn add-class-list [{:keys [class] :as m}]
  (assoc m :class-list (-> class str (str/split #"\s"))))

(defn ->options [[tag maybe-options]]
  (->> (if (map? maybe-options) maybe-options {})
       (join-ids tag)
       (join-classes tag)
       add-class-list))

(defn ->body [hiccup]
  (if (map? (second hiccup))
    (drop 2 hiccup)
    (rest hiccup)))

(defn ->tag [hiccup] (->> hiccup first name (re-find #"^[^#.]*") keyword))
(def split-hiccup (juxt ->tag ->options ->body))
(defn <-hiccup [hiccup] (apply component/->component (split-hiccup hiccup)))
