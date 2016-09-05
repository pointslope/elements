(ns pointslope.elements.datomic
  "The Datomic database component."
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [io.rkn.conformity :as c]))

;; --- helper functions ---

(defn- ensure-norms
  "Ensures that the sequence of norms has been transacted 
  to the database."
  [conn norms]
  (doseq [norm norms]
    (let [loaded (c/read-resource norm)]
      (c/ensure-conforms conn loaded))))

(defn- create-connection
  "Ensures the Datomic database and creates the connection"
  [{:keys [settings]}]
  (try
    (let [{:keys [url norms]} (:database settings)]
      (d/create-database url)

      (let [conn (d/connect url)]
        (when (seq norms)
          (ensure-norms conn norms))
        {:url url :conn conn}))
    (catch Throwable t
      (throw
       (ex-info
        "Unable to create Datomic connection" 
        {:config settings} t)))))

;; --- component implementation -

(defrecord Database [config conn]
  component/Lifecycle
  
  (start [this]
    (if conn
      this
      (conj this (create-connection config))))
  
  (stop [this]
    (if-not conn
      this
      (do
        (d/release conn)
        (assoc this :url nil :conn nil)))))

;; --- public api --- 

(defn new-database
  "Creates the Datomic database component. The component depends on
  a map of configuration settings with the following key structure:
  {:settings 
    {:database 
      {:url 'datomic:mem://example-uri'
       :norms ['db/norms/01-schema-users.edn'
               'db/norms/02-seed-users.edn']}}}
  
  The configurator component in this library makes a great store for
  this configuration data and already stores its information under
  a :settings key.

  (component/system-map
   {:config   (new-configurator)
    :database (component/using (new-database) [:config])})"
  []
  (map->Database {}))

(defn squuid
  "Returns a new Datomic squuid (semi-sequential uuid). This 1-ary
  wrapper is useful as a tagged literal resolver that ignores its 
  value argument. This library provides a data reader definition
  that invokes this resolver:

  (Example)
  {:db/id #db/id[:db.part/user]
   :entity/uuid #db/squuid :new}"
  [_]
  (d/squuid))
