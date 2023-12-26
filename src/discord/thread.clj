(ns discord.thread)

(defn ->Thread [^Runnable task] (Thread. task))
(defn start [^Thread thread] (.start thread))
(defn interrupt [^Thread thread] (.interrupt thread))
