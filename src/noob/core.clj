(ns noob.core)

(defn ->hash-map [key-fn value-fn coll]
  (apply hash-map (mapcat (juxt key-fn value-fn) coll)))

(defn ** [n pow]
  (cond
    (pos? pow) (apply * 1 (repeat pow n))
    (neg? pow) (apply / 1 (repeat (- pow) n))
    :else 1))
