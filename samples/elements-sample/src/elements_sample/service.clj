(ns elements-sample.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.log :as log]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [pointslope.elements.pedestal :as pedestal]
            [pointslope.elements.configurator :as cfg]
            [ring.util.response :as ring-resp]))

(def common-interceptors [(body-params/body-params)
                          http/json-body
                          (pedestal/using-component :config)])

(defn hello
  [_]
  (ring-resp/response {:data "Hello World!"}))

(defn about
  [_]
  (ring-resp/response {:data (format "Clojure %s - served from %s"
                                     (clojure-version)
                                     (route/url-for ::about))}))

(def routes
  #{["/" :get (conj common-interceptors `hello)]
    ["/about" :get (conj common-interceptors `about)]})

(defrecord Service [routes config]
  pedestal/ServiceMapProvider
  (service-map [this]
    (let [reloadable? (cfg/read-setting config :pedestal :reloadable?)
          dev?        (= :dev (cfg/read-setting config :pedestal :service :env))]
      (cond-> config
        true              (cfg/read-setting :pedestal :service)
        reloadable?       (assoc ::http/routes #(route/expand-routes (deref routes)))
        (not reloadable?) (assoc ::http/routes (deref routes))
        dev?              (-> http/default-interceptors http/dev-interceptors)))))

(defn new-service
  ([]
   (new-service #'routes))
  ([routes]
   (map->Service {:routes routes})))
