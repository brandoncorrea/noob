(ns discord.components.core)
(defmulti ->component (fn [kind _options _body] kind))

(defn style-or-default [{:keys [style class-list]} styles default]
  (let [style (if (number? style) style (some-> style name))]
    (or (styles style)
        style
        (some styles class-list)
        default)))

(defn wrap-style [m styles default]
  (assoc m :style (style-or-default m styles default)))