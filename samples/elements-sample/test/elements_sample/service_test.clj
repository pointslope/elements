(ns elements-sample.service-test
  (:require [elements-sample.service :as service]
            [elements-sample.system :as system]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [pointslope.elements.test :refer [with-system]]
            [pointslope.elements.pedestal :as pedestal]
            [clojure.test :refer :all]))

(def url-for (route/url-for-routes (route/expand-routes service/routes)))

(defn test-system
  "A system which does not start an http server."
  []
  (system/app :pedestal-start-fn identity
              :pedestal-stop-fn identity))

(defn service-fn
  "Returns the Pedestal service-fn from the started system."
  [system]
  (pedestal/service-fn (:pedestal system)))

(deftest home-page-test
  (with-system [ts (test-system)]
    (is (=
         (:body (response-for (service-fn ts) :get (url-for ::service/hello)))
         "{\"data\":\"Hello World!\"}"))
    (is (=
         (:headers (response-for (service-fn ts) :get (url-for ::service/hello)))
         {"Content-Type" "application/json;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"}))))

(deftest about-page-test
  (with-system [ts (test-system)]
    (is (.contains
         (:body (response-for (service-fn ts) :get (url-for ::service/about)))
         "Clojure 1.8"))
    (is (=
         (:headers (response-for (service-fn ts) :get (url-for ::service/hello)))
         {"Content-Type" "application/json;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"}))))
