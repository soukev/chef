(ns chef.recipe
  "Functionality for parsing recipes."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [chef.executor :as cr]))

(defn load-recipe
  "Loads recipe from given file path."
  [file-path]
  (try
    (with-open [r (io/reader file-path)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException e
      (throw (Exception. (format "Couldn't open '%s': %s\n" file-path (.getMessage e)))))
    (catch RuntimeException e
      (throw (Exception. (format "Error parsing edn file '%s': %s\n" file-path (.getMessage e)))))))

(defn run-recipe-step
  [step]
  (let [run #(cr/run-command! (cr/command (:command step) %))]
    (if (:run-command-for-each-package step)
      (doseq [package (:packages step)]
        (run package))
      (run (:packages step)))))

(defn run-recipe
  [recipe]
  (doseq [step recipe]
    (run-recipe-step step)))
