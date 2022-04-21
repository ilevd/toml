(ns toml.core-test
  (:require [clojure.instant :refer [read-instant-timestamp]]
            [clojure.test :refer :all]
            [toml.core :as toml]))

;; tests from https://github.com/lantiga/clj-toml


(deftest comment-test
  (testing "Comments"
    (is (= (toml/read "#just a comment line
                          foo = \"bar\" # and one more comment")
           {"foo" "bar"}))))


(deftest numbers-test
  (testing "Numbers"
    (is (= (toml/read "integer = 3_0
                          negative_integer = -3_0
                          float = 3_0.0
                          float_exp = 1e1_0
                          negative_float = -3.0")
           {"integer"          30
            "negative_integer" -30
            "float"            30.0
            "float_exp"        1e10
            "negative_float"   -3.0}))))


(deftest bool-test
  (testing "Booleans"
    (is (= (toml/read "truthy = true
                       falsy = false")
           {"truthy" true
            "falsy"  false}))))


(deftest datetime-test
  (testing "Datetime"
    (is (= (toml/read "mydob = 1975-10-03T16:20:00Z # and a comment, just because")
           {"mydob" (read-instant-timestamp "1975-10-03T16:20:00Z")}))
    ;; this fails
    ;(is (= (toml/read  "mydob = 1975-10-03T16:20:00.999999Z # and a comment, just because")
    ;       {"mydob" (read-instant-timestamp "1975-10-03T16:20:00.999999Z")}))
    (is (= (toml/read "mydob = 1975-10-03T16:20:00-07:00 # and a comment, just because")
           {"mydob" (read-instant-timestamp "1975-10-03T16:20:00-07:00")}))
    ;; this fails
    ;(is (= (toml/read  "mydob = 1975-10-03T16:20:00.999999-07:00 # and a comment, just because")
    ;       {"mydob" (read-instant-timestamp "1975-10-03T16:20:00.999999-07:00")}))
    ))


(deftest array-test
  (testing "Arrays"
    (is (= (toml/read "inline = [1, 2, 3]
                          multiline = [4,
                                       5,
                                       -6]
                          nested = [[7, 8, -9],
                                    [\"seven\",
                                     \"eight\",
                                     \"negative nine\"]]")
           {"inline"    [1 2 3]
            "multiline" [4 5 -6]
            "nested"    [[7 8 -9] ["seven" "eight" "negative nine"]]}))))


(deftest lonely-keygroup
  (testing "Lonely keygroups"
    (is (= (toml/read "[Agroup]
                          [Bgroup]
                          [Bgroup.nested]
                          [Cgroup.nested]")
           {"Agroup" {}
            "Bgroup" {"nested" {}}
            "Cgroup" {"nested" {}}}))))


(deftest standard-keygroup
  (testing "Standard keygroups"
    (is (= (toml/read "[Agroup]
                          first = \"first\"
                          second = true
                          third = 3
                          fourth = 4.0
                          fifth = [5, -6 ,7]")
           {"Agroup" {"first"  "first"
                      "second" true
                      "third"  3
                      "fourth" 4.0
                      "fifth"  [5 -6 7]}}))))


(deftest nested-keygroup
  (testing "Nested keygroups"
    (is (= (toml/read "[Agroup]
                          first = \"first\"
                          second = true
                          [Agroup.nested]
                          third = 3
                          fourth = 4.0
                          [Bgroup.nested]
                          fifth = [5, -6 ,7]")
           {"Agroup" {"first"  "first"
                      "second" true
                      "nested"
                               {"third"  3
                                "fourth" 4.0}}
            "Bgroup" {"nested"
                      {"fifth" [5 -6 7]}}}))))


(deftest inline-table-test
  (testing "Inline table"
    (is (= (toml/read "[table.inline]
                          name = { first = \"Tom\", last = \"Preston-Werner\" }
                         ")
           {"table" {"inline" {"name" {"first" "Tom"
                                       "last"  "Preston-Werner"}}}}))))


(deftest example-test
  (testing "TOML example"
    (is (= (toml/read (slurp "resources/example.toml"))
           {"title"    "TOML Example"
            "owner"    {"dob" #inst"1979-05-27T15:32:00.000-00:00", "name" "Tom Preston-Werner"},
            "database" {"server" "192.168.1.1", "connection_max" 5000, "ports" [8001 8001 8002], "enabled" true},
            "servers"  {"alpha" {"ip" "10.0.0.1", "dc" "eqdc10"}, "beta" {"ip" "10.0.0.2", "dc" "eqdc10"}},
            "clients"  {"data" [["gamma" "delta"] [1 2]], "hosts" ["alpha" "omega"]},
            "storages" [{"name" "Redis", "priority" 10} {"name" "Memcached", "priority" 5}]
            }))))


(deftest hard-example-test
  (testing "TOML hard example"
    (is (= (toml/read (slurp "resources/hard_example.toml"))
           {"the"
            {"hard"
                           {"another_test_string" " Same thing, but with a string #",
                            "test_array2"
                                                  ["Test #11 ]proved that" "Experiment #9 was a success"],
                            "test_array"          ["] " " # "],
                            ;; next fails
                            ;"bit#"
                            ;                      {"what?" "You don't think some user won't do that?",
                            ;                       "multi_line_array" ["]"]},
                            "harder_test_string"
                                                  " And when \"'s are in the string, along with # \""},
             "test_string" "You'll hate me after this - #"}}))))



(deftest read-write
  (testing "Read - write"
    (is (let [data {:a 1
                    :b {:c 3
                        :d 4
                        :e {:a 1}}
                    :d "hello"}]
          (= data (toml/read (toml/write data) :keywordize))))))


