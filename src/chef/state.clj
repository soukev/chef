(ns chef.state)

(def ^:private state (atom {}))

(defn get-state!
  [key]
  (@state key))

(defn update-state!
  [key val]
  (swap! state assoc key val))
