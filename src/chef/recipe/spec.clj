(ns chef.recipe.spec
  "Spec definition of recipe format."
  (:require [clojure.spec.alpha :as s]))

;; STEP
;; Required
(s/def :step/command string?)

;; Optional
(s/def :step/arguments (s/coll-of string? :kind vector?))
(s/def :step/run-command-for-each-argument boolean?)

;; Step map
(s/def :step/step
  (s/keys :req-un [:step/command]
          :opt-un [:step/arguments :step/run-command-for-each-argument]))

;; RECIPE
;; Required
(s/def :recipe/steps (s/coll-of :step/step))

;; Optional
(s/def :recipe/name string?)
(s/def :recipe/version string?)

;; Recipe map
(s/def :recipe/recipe
  (s/keys :req-un [:recipe/steps]
          :opt-un [:recipe/name :recipe/version]))

(defn validate
  [recipe]
  (s/valid? :recipe/recipe recipe))

(defn explain
  [invalid-recipe]
  (s/explain :recipe/recipe invalid-recipe))
