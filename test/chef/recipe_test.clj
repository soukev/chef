(ns chef.recipe-test
  (:require [chef.recipe :as sut]
            [clojure.test :as t]))

(t/deftest load-non-existent-file-throws-test
  (t/testing "Loading non existent recipe file throws."
    (t/is (thrown-with-msg? Exception #"Couldn't open" (sut/load-recipe "non-existent-file-that-throws.edn")))))
