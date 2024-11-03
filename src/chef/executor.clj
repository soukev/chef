(ns chef.executor
  (:require [clojure.string :as str]
            [clojure.java.process :as proc]))

(defn split-and-flatten
  [v]
  (vec (mapcat #(str/split % #"\s+") v)))

(defmulti command (fn [_ args] (class args)))
(defmethod command clojure.lang.PersistentVector
  [command args]
  (-> (into [command] args)
      split-and-flatten))
(defmethod command java.lang.String
  [command args]
  (-> [command args]
      split-and-flatten))
(defmethod command nil
  [command _]
  (-> [command]
      split-and-flatten))

(defn run-command! [command]
    (try
        ;; (let [pb (doto (java.lang.ProcessBuilder. command)
        ;;             (.redirectInput java.lang.ProcessBuilder$Redirect/INHERIT)
        ;;             (.redirectOutput java.lang.ProcessBuilder$Redirect/INHERIT)
        ;;             (.redirectError java.lang.ProcessBuilder$Redirect/INHERIT))
        ;;     ;; Start and wait for the process to complete
        ;;         process (.start pb)]
        ;;     (.waitFor process))
      (let [process (-> (apply proc/start
                            {:in :inherit
                             :out :inherit
                             :err :inherit}
                            command)
                        proc/exit-ref)]
          @process
          nil)
  (catch java.io.IOException e
        (throw (ex-info (format "Couldn't execute the '%s' command." (str/join " " command)) {:command command :original-msg (ex-message e)})))))
