(ns user
  (:require
    [figwheel-sidecar.repl-api :as f]
    [reloaded.repl :refer [system reset stop]]
    [danuraisite.system]))

(reloaded.repl/set-init! #'danuraisite.system/create-system)

;; user is a namespace that the Clojure runtime looks for and
;; loads if its available

;; You can place helper functions in here. This is great for starting
;; and stopping your webserver and other development services

;; The definitions in here will be available if you run "lein repl" or launch a
;; Clojure repl some other way

;; You have to ensure that the libraries you :require are listed in your dependencies

;; Once you start down this path
;; you will probably want to look at
;; tools.namespace https://github.com/clojure/tools.namespace
;; and Component https://github.com/stuartsierra/component


(defn fig-start
  "This starts the figwheel server and watch based auto-compiler."
  []
  ;; this call will only work are long as your :cljsbuild and
  ;; :figwheel configurations are at the top level of your project.clj
  ;; and are not spread across different lein profiles

  ;; otherwise you can pass a configuration into start-figwheel! manually
  ;; https://cljdoc.org/d/figwheel-sidecar/figwheel-sidecar/0.5.18/api/figwheel-sidecar.repl-api#start-figwheel!
  (f/start-figwheel! "genesys-dev","mwl-dev" "lugs-dev" "hsl-dev" "don-dev" "mwl-dev" "nrf-dev" "kasei-dev" "scores-dev" "kt-dev" ))

(defn fig-stop
  "Stop the figwheel server and watch based auto-compiler."
  []
  (f/stop-figwheel!))

;; if you are in an nREPL environment you will need to make sure you
;; have setup piggieback for this to work
(defn cljs-repl
  "Launch a ClojureScript REPL that is connected to your build and host environment."
  []
  (f/cljs-repl))
