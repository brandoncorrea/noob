(ns discord.component
  (:require [c3kit.apron.corec :as ccc]
            [clojure.string :as str]
            [discord.components.button]
            [discord.components.core :as components]
            [discord.components.input]))

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

(def join-ids (partial join-keys #"#[^\.#]*" [:custom_id :id]))
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

(defn ->tag [hiccup] (->> hiccup first name (re-find #"^[a-zA-Z]*") keyword))
(def split-hiccup (juxt ->tag ->options ->body))
(defn <-hiccup [hiccup] (apply components/->component (split-hiccup hiccup)))

(defmethod components/->component :tr [_ _ body]
  {:type 1 :components (map <-hiccup body)})
