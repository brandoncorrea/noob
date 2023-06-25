(ns discord.components.option
  (:require [discord.components.component :as component]
            [discord.core :as core]))

(defn default? [{:keys [default selected class-list] :as m}]
  (or default
      (and (not (contains? m :default))
           (or selected
               (some #{"default" "selected"} class-list)))))

(defn wrap-default [m]
  (if (default? m)
    (assoc m :default true)
    (dissoc m :default)))

(defn conform [m]
  (-> m wrap-default (select-keys [:label :description :value :default])))

(defmethod component/->component :option [_ options [label]]
  (-> options (core/assoc-unless :label label) conform))
