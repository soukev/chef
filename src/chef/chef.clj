(ns chef.chef
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [chef.recipe :as cr]
            [chef.state :as cs]))

(def cli-options
  [
   ["-v" "--verbose"]
   ["-h" "--help"]
   ])

(defn usage
  [options-summary]
  (->> ["chef"
        "----"
        ""
        "Usage: chef [options] recipe.edn"
        ""
        "Options:"
        options-summary
        ]
       (string/join \newline)))

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-arguments
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) {:exit-message (usage summary) :ok? true}
      errors {:exit-message (error-msg errors)}
      (= 1 (count arguments)) {:recipe (first arguments) :options options}
      :else {:exit-message (usage summary)})))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn run
  [recipe options]
  (when (:verbose options)
    (cs/update-state! :verbose true))
  (-> recipe
      cr/load-recipe
      cr/run-recipe!))

(defn -main
  [& args]
  (let [{:keys [recipe options exit-message ok?]} (validate-arguments args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (run recipe options))))
