(ns discord.option)

(defn- create [required type name description]
  {:required    required
   :type        type
   :name        name
   :description description})

(def ->string (partial create false 3))
(def ->string! (partial create true 3))
(def ->int (partial create false 4))
(def ->int! (partial create true 4))
(def ->bool (partial create false 5))
(def ->bool! (partial create true 5))
(def ->user (partial create false 6))
(def ->user! (partial create true 6))
(def ->channel (partial create false 7))
(def ->channel! (partial create true 7))
(def ->role (partial create false 8))
(def ->role! (partial create true 8))
(def ->mentionable (partial create false 9))
(def ->mentionable! (partial create true 9))
(def ->double (partial create false 10))
(def ->double! (partial create true 10))
(def ->attachment (partial create false 11))
(def ->attachment! (partial create true 11))

(defn get-option [request key] (get-in request [:data :options key]))

(def ->option-type
  {:string      3
   :int         4
   :long        4
   :bool        5
   :boolean     5
   :user        6
   :channel     7
   :role        8
   :mentionable 9
   :double      10
   :float       10
   :attachment  11})

(defn schema->option-spec [k type api]
  (-> (merge {:type type :name k :required false} api)
      (update :type ->option-type)
      (update :required boolean)
      (update :name name)))

(defn <-spec [k {:keys [type api]}]
  (let [{:keys [required type name description]} (schema->option-spec k type api)]
    (create required type name description)))
