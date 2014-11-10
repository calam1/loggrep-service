(ns loggrep-service.core.filter
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn parse-line-ocp
  [str]
  (let [[_ date time thread_id remote_host uri log_level logger value] (re-find  #"^(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(\S+)\s+(.+)$" str)]
    [date time thread_id remote_host uri log_level logger value]))


(defn group-by-date
  ([grouping lines]
    (group-by-date grouping lines {}))
  ([grouping lines state]
    (lazy-seq
     (when (seq lines)
       (let [[date time thread_id remote_host uri log_level logger value] (parse-line-ocp (first lines))
                             ;creates s nested map of date and time
                            state (assoc-in state [date "data"]
                                            (conj (get-in state [date "data"] []) (str date " " time " " thread_id " " remote_host " " uri " " log_level " " logger " " value)))]
         (if (= grouping date)
            (cons (get state date) (group-by-date grouping (rest lines)
                                                     (dissoc state date)))
            (group-by-date grouping (rest lines) state)))))))


(defn group-by-thread-id
  ([grouping lines]
    (group-by-thread-id grouping lines {}))
  ([grouping lines state]
    (lazy-seq
     (when (seq lines)
       (let [[date time thread_id remote_host uri log_level logger value] (parse-line-ocp (first lines))
                            state (assoc-in state [thread_id "data"]
                                            (conj (get-in state [thread_id "data"] []) (str date " " time " " thread_id " " remote_host " " uri " " log_level " " logger " " value)))]
         (if (= grouping thread_id)
            (cons (get state thread_id) (group-by-thread-id grouping (rest lines)
                                                     (dissoc state thread_id)))
            (group-by-thread-id grouping (rest lines) state)))))))

(defn filter-by-attribute
  [group-by-attribute]
  (fn [grouping file]
    (with-open [logfile (io/reader file)]
      (doall
        (group-by-attribute grouping (line-seq logfile))))))

(def parse-log-file-by-date
  (filter-by-attribute group-by-date))

(def parse-log-file-by-thread-id
  (filter-by-attribute group-by-thread-id))

(defn hardcoded-search-values
  []
  (json/write-str (parse-log-file-by-date "2014-10-23" "data/cat.out")))
