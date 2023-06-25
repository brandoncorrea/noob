(ns discord.components.select
  (:require [discord.components.component :as component]
            [discord.components.hiccup :as hiccup]
            [discord.components.option :as option]))

(def base-keys
  [:custom_id
   :disabled
   :placeholder
   :min_values
   :max_values
   :type])

(def types {"user"        5
            "role"        6
            "mentionable" 7
            "channel"     8})

(def channel-types
  {"guild-text"          0
   "dm"                  1
   "guild-voice"         2
   "group-dm"            3
   "guild-category"      4
   "guild-announcement"  5
   "announcement-thread" 10
   "public-thread"       11
   "private-thread"      12
   "guild-stage-voice"   13
   "guild-directory"     14
   "guild-forum"         15
   })

(defn wrap-values [{:keys [min-values max-values values] :as m}]
  (let [min-values (or min-values values)
        max-values (or max-values values)]
    (cond-> m
            min-values (assoc :min_values min-values)
            max-values (assoc :max_values max-values))))

(defn ->select-options [{:keys [options]} body]
  (if options
    (map option/conform options)
    (map hiccup/<-hiccup body)))

(defn wrap-options [m body]
  (assoc m :options (->select-options m body)))

(defn wrap-text [m body]
  (-> m
      (wrap-options body)
      (select-keys (conj base-keys :options))))

(defn resolve-type [{:keys [class-list type]}]
  (or (when (number? type) type)
      (some-> type name types)
      (some types class-list)
      3))

(defn resolve-channel [channel]
  (if (number? channel)
    channel
    (some-> channel name channel-types)))

(def xf-channels (comp (map resolve-channel) (remove nil?)))
(defn join-channels [parsed-channels & unparsed-colls]
  (->> (apply concat unparsed-colls)
       (transduce xf-channels conj parsed-channels)
       distinct))

(defn wrap-channels [{:keys [channel-types channels class-list] :as m}]
  (let [class-channels (into [] xf-channels class-list)]
    (cond-> m
            (or channel-types channels (seq class-channels))
            (assoc :channel_types (join-channels class-channels channel-types channels)))))

(defn wrap-channel [m]
  (-> m
      wrap-channels
      (select-keys (conj base-keys :channel_types))))

(defn wrap-type [m body]
  (let [m (assoc m :type (resolve-type m))]
    (case (:type m)
      3 (wrap-text m body)
      8 (wrap-channel m)
      (select-keys m base-keys))))

(defmethod component/->component :select [_ options body]
  (-> options
      wrap-values
      component/wrap-disabled
      (wrap-type body)))
