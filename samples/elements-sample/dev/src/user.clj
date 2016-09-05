(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [elements-sample.system :refer [app]]
            [com.stuartsierra.component :as component]))

(reloaded.repl/set-init! #(app))
