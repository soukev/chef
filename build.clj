(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.deps :as t]
            [clojure.tools.build.api :as b]
            [clojure.java.shell :refer [sh]]))

(def lib 'net.clojars.chef/chef)
(def uberjar-name 'chef)
(def version "0.1.0-SNAPSHOT")
(def uberfile-target (format "target/%s-%s.jar" uberjar-name version))
(def main 'chef.chef)
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (println "\nRunning tests...")
  (let [basis    (b/create-basis {:aliases [:test]})
        combined (t/combine-aliases basis [:test])
        cmds     (b/java-command
                  {:basis basis
                   :java-opts (:jvm-opts combined)
                   :main      'clojure.main
                   :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn clean
  [_]
  (println "\nCleaning target directory...")
  (b/delete {:path "target"}))

(defn- uber-opts [opts]
  (assoc opts
         :main main
         :uber-file uberfile-target
         :basis (b/create-basis {})
         :class-dir class-dir
         :src-dirs ["src"]
         :ns-compile [main]))

(defn uberjar
  "Build uberjar"
  [opts]
  (clean nil)
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding JAR...")
    (b/uber opts)))

(defn compile-native
  "Compile to native image with GraalVM."
  [opts]
  (uberjar opts)
  (println "\nCompiling to native image...")
  (let [{:keys [exit out err]} (sh "native-image"
                                   "--features=clj_easy.graal_build_time.InitClojureClasses"
                                   "-jar" uberfile-target
                                   "--no-fallback" "--no-server"
                                   "target/chef")]
    (if (zero? exit)
      (println "Native image built successfully!")
      (do (println "Error building native image:" err)
          (throw (ex-info "native-image build failed" {:exit exit :out out :err err}))))))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]
  (test opts)
  (compile-native opts)
  opts)
