(ns elements-sample.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [elements-sample.system :as system]
            [io.pedestal.log :as log]))

(defn -main
  "App entry point"
  [& args]
  (log/maybe-init-java-util-log)
  (log/info :msg "Starting app...")
  (component/start (system/app)))
