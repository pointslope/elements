(ns pointslope.elements.test
  "Helpers for testing Component systems."
  (:require [com.stuartsierra.component :as component]))

(defmacro with-system
  "Binds the var to the result of the binding expression,
  which should be a valid component system. Starts the
  system prior to evaluating the body expression. Stops
  the system after the body has been evaluated.

  Example usage:

  (deftest some-test
      (with-system [test-system (my-test-system-init-fn)]
        (let [service (pedestal/service-fn (:pedestal test-system))
              response (response-for service :get \"/\")]
          (is (= 200 (:status response))))))
"
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))
