(ns chef.print
  (:require [chef.state :as cs]))

(defn- print-func-if-verbose
  [func msg]
  (when (cs/get-state! :verbose)
    (func msg)))

(defn print-if-verbose
  [msg]
  (print-func-if-verbose print msg))

(defn println-if-verbose
  [msg]
  (print-func-if-verbose println msg))
