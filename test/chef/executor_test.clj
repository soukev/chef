(ns chef.executor-test
  (:require [chef.executor :as sut]
            [clojure.test :as t]))

(t/deftest run-command-non-existent-throws
  (t/testing "Running non existent command throws exception."
    (t/is (thrown-with-msg? Exception #"Couldn't execute" (sut/run-command! (sut/command "llllll" []))))))

(t/deftest command-vector
  (t/testing "Building command with vector of arguments."
    (t/is (=
           ["command" "param" "argument" "argument2"]
           (sut/command "command param" ["argument" "argument2"])))))

(t/deftest command-string
  (t/testing "Building command with string argument."
    (t/is (=
           ["command" "argument"]
           (sut/command "command" "argument")))))

(t/deftest command-nil
  (t/testing "Building command with no arguments (nil)."
    (t/is (=
          ["command"]
          (sut/command "command" nil)))))
