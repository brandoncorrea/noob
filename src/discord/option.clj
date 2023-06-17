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