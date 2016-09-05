(ns elements-sample.service-test
  (:require [elements-sample.service :as service]
            [elements-sample.system :as system]
            [io.pedestal.test :refer [response-for]]
            [pointslope.elements.test :refer [with-system]]
            [pointslope.elements.pedestal :as pedestal]
            [clojure.test :refer :all]))

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
         (:body (response-for (service-fn ts) :get "/"))
         "Hello World!"))
    (is (=
         (:headers (response-for (service-fn ts) :get "/"))
         {"Content-Type" "text/html;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"}))))

(deftest about-page-test
  (with-system [ts (test-system)]
    (is (.contains
         (:body (response-for (service-fn ts) :get "/about"))
         "Clojure 1.8"))
    (is (=
         (:headers (response-for (service-fn ts) :get "/about"))
         {"Content-Type" "text/html;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"}))))
