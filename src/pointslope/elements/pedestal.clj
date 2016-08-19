(ns pointslope.elements.pedestal
  "Component-friendly implementation of Pedestal."
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :as interceptor]))

;; --- Service Map Provider ---

(defprotocol ServiceMapProvider
  (service-map [this] "Provides a service map."))

(extend-protocol ServiceMapProvider
  clojure.lang.APersistentMap
  (service-map [this] this)

  nil
  (service-map [_] {}))

;; --- Pedestal component helpers ---

(def attribution-meta
  "Metadata attributing Stuart Sierra's component.pedestal"
  {:author "Stuart Sierra"
   :repo "https://github.com/stuartsierra/component.pedestal"})

(defn ^attribution-meta insert-context-interceptor
  "Returns an interceptor which associates key with value in the
  Pedestal context map."
  [key value]
  (interceptor/interceptor
   {:name ::insert-context
    :enter (fn [context] (assoc context key value))}))

(defn- ^attribution-meta get-pedestal
  "Retrieves the Pedestal component from the context.
  Throws an exception if it isn't found."
  [context]
  (let [pedestal (::pedestal context)]
    (when-not pedestal
      (throw (ex-info (str "Pedestal component was nil in context map; "
                           "component.pedestal is not configured correctly")
                      {:reason ::nil-pedestal
                       :context context})))
    pedestal))

(defn ^attribution-meta context-component
  "Returns the component at key from the Pedestal context map. key
  must have been a declared dependency of the Pedestal server
  component."
  [context key]
  (let [component (get (get-pedestal context) key ::not-found)]
    (when (nil? component)
      (throw (ex-info (str "Component " key " was nil in Pedestal dependencies; "
                           "maybe it returned nil from start or stop")
                      {:reason ::nil-component
                       :dependency-key key
                       :context context})))
    (when (= ::not-found component)
      (throw (ex-info (str "Missing component " key " from Pedestal dependencies")
                      {:reason ::missing-dependency
                       :dependency-key key
                       :context context})))
    component))

(defn ^attribution-meta using-component
  "Returns an interceptor which associates the component named key
  into the Ring-style request map as :component. The key must have
  been declared a dependency of the Pedestal server component.
  You can add this interceptor to your Pedestal routes to make the
  component available to your Ring-style handler functions, which can
  get :component from the request map."
  ([key]
   (using-component key key))

  ([pedestal-key request-key]
   (interceptor/interceptor
    {:name ::using-component
     :enter (fn [context]
              (assoc-in context [:request request-key]
                        (context-component context pedestal-key)))})))

;; --- component implementation ---

(defrecord Pedestal [service-map-provider start-fn stop-fn service]
  component/Lifecycle
  (start [this]
    (if service
      this
      (-> service-map-provider
          service-map
          http/default-interceptors
          (update ::http/interceptors conj (insert-context-interceptor ::pedestal this))
          (http/create-server)
          (start-fn)
          ((partial assoc this :service)))))
  (stop [this]
    (when service
      (stop-fn service))
    (assoc this :service nil)))

(defn new-pedestal
  ([]
   (new-pedestal http/start http/stop))

  ([start-fn stop-fn]
   (map->Pedestal {:start-fn start-fn
                   :stop-fn  stop-fn})))
