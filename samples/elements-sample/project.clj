(defproject elements-sample "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ "1.1.0"]
                 [pointslope/elements "0.1.0"
                  :exclusions [[environ]
                               [org.slf4j/slf4j-api]
                               [org/slf4j/slf4j-nop]
                               [io.pedestal/pedestal.service]
                               [io.pedestal/pedestal.jetty]
                               [com.datomic/datomic-free]]]
                 [com.stuartsierra/component "0.3.1"]
                 [com.datomic/datomic-free "0.9.5394"]
                 [io.pedestal/pedestal.service "0.5.1"]
                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.1"]
                 ;; [io.pedestal/pedestal.immutant "0.5.1"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.1"]

                 [ch.qos.logback/logback-classic "1.1.7" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.21"]
                 [org.slf4j/jcl-over-slf4j "1.7.21"]
                 [org.slf4j/log4j-over-slf4j "1.7.21"]]
  :min-lein-version "2.0.0"
  :source-paths ["src"]
  :resource-paths ["config" "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.3"]]
  :profiles {:dev {:source-paths ["dev/src" "src"]
                   :aliases {"run-dev" ["trampoline" "run" "-m" "reloaded.repl/go"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.1"]
                                  [reloaded.repl "0.2.2"]]
                   :repl-options {:init-ns user}}
             :uberjar {:aot [elements-sample.main]}}
  :main ^{:skip-aot true} elements-sample.main)
