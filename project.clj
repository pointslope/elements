(defproject pointslope/elements "0.1.0"
  :description "Reusable elements for component-based Clojure applications"
  :url "https://github.com/pointslope/elements"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.3.1"]
                 [environ "1.1.0"]
                 [levand/immuconf "0.1.0"]
                 [com.datomic/datomic-free "0.9.5394"]
                 [io.rkn/conformity "0.4.0"]
                 [io.pedestal/pedestal.service "0.5.0"]]
  :repositories [["clojars" {:url "https://clojars.org/repo/" :creds :gpg}]]
  :deploy-repositories [["releases" :clojars]]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.8.0"]
                                  [io.pedestal/pedestal.jetty "0.5.0"]]}})
