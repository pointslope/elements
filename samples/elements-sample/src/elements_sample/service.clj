(ns elements-sample.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.log :as log]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [pointslope.elements.pedestal :as pedestal]
            [pointslope.elements.configurator :as cfg]
            [ring.util.response :as ring-resp]))

(def common-interceptors [(body-params/body-params)
                          http/html-body
                          (pedestal/using-component :config)])

(def hello
  (interceptor/interceptor
   {:name ::greeting
    :enter (fn [context]
             (assoc context
                    :response
                    (ring-resp/response "Hello World!")))}))

(def about
  (interceptor/interceptor
   {:name ::about
    :enter (fn [context]
             (assoc context
                    :response
                    (ring-resp/response (format "Clojure %s - served from %s"
                                                (clojure-version)
                                                (route/url-for ::about)))))}))

(def routes
  #{["/" :get (conj common-interceptors hello) :route-name ::greeting]
    ["/about" :get (conj common-interceptors about) :route-name ::about]})

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
