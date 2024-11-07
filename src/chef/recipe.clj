(ns chef.recipe
  "Functionality for parsing recipes."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [chef.executor :as cr]
            [chef.recipe.spec :as spec]
            [chef.print :refer [println-if-verbose]]))

(defn load-recipe
  "Loads recipe from given file path."
  [file-path]
  (try
    (with-open [r (io/reader file-path)]
      (let [recipe (edn/read (java.io.PushbackReader. r))]
        (when (not (spec/validate recipe))
          (throw (ex-info "Invalid recipe." {:cause (spec/explain recipe)})))
        recipe))
    (catch java.io.IOException e
      (throw (Exception. (format "Couldn't open '%s': %s\n" file-path (.getMessage e)))))
    (catch RuntimeException e
      (throw (Exception. (format "Error parsing edn file '%s': %s\n" file-path (.getMessage e)))))))

(defn run-recipe-step!
  [step]
  (when (:desc step)
    (println-if-verbose (:desc step)))
  (let [run #(cr/run-command! (cr/command (:command step) %))]
    (if (:run-command-for-each-package step)
      (doseq [package (:arguments step)]
        (run package))
      (run (:arguments step)))))

(defn run-recipe!
  [recipe]
  (let [steps (:steps recipe)
        name (:name recipe)
        version (:version recipe)]
    (when name
      (println-if-verbose name))
    (when version
      (println-if-verbose (str "Version: " version "\n")))
    (println-if-verbose "Starting to run steps...")
    (doseq [step steps]
      (run-recipe-step! step))))
