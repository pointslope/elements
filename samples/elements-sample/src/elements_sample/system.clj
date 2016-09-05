(ns elements-sample.system
    (:require [com.stuartsierra.component :as component]
              [pointslope.elements.pedestal :as pedestal-component]
              [pointslope.elements.configurator :as config-component]
              [io.pedestal.http :as http]
              [elements-sample.service :as service]))

(defn app
  ([]
   (app :pedestal-start-fn http/start :pedestal-stop-fn http/stop))
  ([& opts]
   (let [{:keys [pedestal-start-fn pedestal-stop-fn]} (apply hash-map opts)]
     (component/system-map
      :config (config-component/new-configurator)
      :service (component/using
                (service/new-service)
                [:config])
      :pedestal (component/using
                 (pedestal-component/new-pedestal pedestal-start-fn pedestal-stop-fn)
                 {:service-map-provider :service
                  :config :config})))))
