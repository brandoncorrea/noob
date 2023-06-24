(ns discord.core)

(defn assoc-unless [m k v]
  (cond-> m
          (and (some? v) (not (contains? m k)))
          (assoc k v)))
