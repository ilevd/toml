(ns toml.core
  (:refer-clojure :exclude [read])
  (:require [clojure.string :as str])
  (:import (com.moandjiezana.toml Toml)
           (java.util HashMap ArrayList))
  (:gen-class))


;;======================================================================================================================
;; Reader
;; =====================================================================================================================
(defn- java->clojure* [data {keywordize :keywordize :as params}]
  (cond
    ;; hashmap -> map
    (instance? java.util.HashMap data) (reduce (fn [res next] (assoc res (if (:keywordize params)
                                                                           (keyword next)
                                                                           next)
                                                                         (java->clojure* (get data next) params)))
                                               {}
                                               (into [] (.keySet data)))
    ;; array -> vec
    (instance? java.util.ArrayList data) (mapv #(java->clojure* % params) data)
    :else data))


(defn java->clojure [data & params]
  (java->clojure* data (reduce #(assoc %1 %2 true) {} params)))


(defn clojure->java [data]
  (cond
    ;; map -> HashMap
    (map? data) (let [res (HashMap.)]
                  (doseq [[key val] data]
                    (.put res
                          (if (keyword? key) (name key) key)
                          (clojure->java val)))
                  res)
    ;; seq -> ArrayList
    (sequential? data) (let [res (ArrayList.)]
                         (doseq [val data]
                           (.add res (clojure->java val)))
                         res)
    :else data))


(defn read [str & params]
  (let [toml        (Toml.)
        java-result (.toMap (.read toml str))]
    (apply java->clojure (cons java-result params))))


;;======================================================================================================================
;; Writer
;; =====================================================================================================================
(defn- print-name [name sb]
  (when (seq name)
    (.append sb "\n[")
    (.append sb name)
    (.append sb "]\n")))


(defn- compose-deep-name [deep-name nested-name]
  (let [deep-name   (name deep-name)
        nested-name (name nested-name)]
    (if (str/blank? deep-name)
      nested-name
      (str deep-name "." nested-name))))


(defn- print-val [val sb]
  (cond
    (map? val) (do (.append sb "{ ")
                   (doseq [[idx [k v]] (map-indexed vector val)]
                     (when (> idx 0)
                       (.append sb ", "))
                     (.append sb (name k))
                     (.append sb " = ")
                     (print-val v sb))
                   (.append sb " }"))
    (sequential? val) (do (.append sb "[")
                          (doseq [[idx v] (map-indexed vector val)]
                            (when (> idx 0)
                              (.append sb ", "))
                            (print-val v sb))
                          (.append sb "]"))
    true (.append sb (pr-str val))))


(defn write
  ([data] (write data "" (StringBuilder.)))
  ([data deep-name sb]
   (let [{simple-vals false complex-vals true} (group-by (comp map? second) data)]
     (when (seq simple-vals)
       (print-name deep-name sb)
       (doseq [[k v] simple-vals]
         (.append sb (name k))
         (.append sb " = ")
         (print-val v sb)
         (.append sb "\n")))
     (doseq [[dp v] complex-vals]
       (write v (compose-deep-name deep-name dp) sb))
     (.toString sb))))
