(ns toml.core
  (:import
    ;[com.electronwill.toml Toml]
    (com.moandjiezana.toml Toml)
    (java.util HashMap ArrayList))
  (:gen-class))

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
  (prn params)
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

;(defn read [str & params]
;  (apply java->clojure (cons (Toml/read str) params)))
;
;(defn write [data]
;  (-> data clojure->java Toml/writeToString))

(defn read [str & params]
  (let [toml (Toml.)
        java-result (.toMap (.read toml str))]
    (apply java->clojure (cons java-result params))))

