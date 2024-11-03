(ns chef.chef
  (:gen-class)
  (:require [chef.recipe :as cr]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> (first args)
      cr/load-recipe
      cr/run-recipe))
