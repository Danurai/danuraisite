(defproject danuraisite "0.1.0-SNAPSHOT"
  :description "danuraisite"
  :url "https://github.com/Danurai/danurai.github.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"
  
  :main         danuraisite.system

  :jar-name     "danuraisite.jar"
  :uberjar-name "danuraisite-standalone.jar"
  
  :repl-options {:init-ns user
                 :timeout 120000}
  
  :dependencies [[org.clojure/clojure "1.10.0"]
                [org.clojure/clojurescript "1.10.520"]
                [org.clojure/core.async  "0.3.443"]
                [funcool/octet "1.1.2"]
                ; Web server
                [http-kit "2.3.0"]
                [com.stuartsierra/component "0.3.2"]
                ; routing
                [compojure "1.6.0"]
                [ring/ring-defaults "0.3.1"]
                [clj-http "3.7.0"]
                ; Websocket sente
                ; [com.taoensso/sente "1.12.0"]
                ; page rendering
                [hiccup "1.0.5"]
                [cljs-http "0.1.46"]
								; [cheshire "5.8.0"]
                [reagent "0.7.0"]
                ; user management
                [com.cemerick/friend "0.2.3"]
                ; Databasing
                [org.clojure/java.jdbc "0.7.5"]
                [org.xerial/sqlite-jdbc "3.7.2"]
                [org.postgresql/postgresql "9.4-1201-jdbc41"]]

  :plugins [[lein-figwheel "0.5.14"]
           [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
           [lein-autoexpect "1.9.0"]]

  :source-paths ["src/clj"]

;; https://github.com/emezeske/lein-cljsbuild
;; https://stackoverflow.com/questions/29445260/compile-multiple-cljs-files-to-independent-js-files
  :cljsbuild {
    :builds {
      :lugs-dev {
        :source-paths ["src/cljs-lugs"]
        :figwheel true
        :compiler {
          :main       danuraisite.lugscore
          :asset-path "/js/compiled/out-lugs"
          :output-to  "resources/public/js/compiled/lugs-app.js"
          :output-dir "resources/public/js/compiled/out-lugs"
          :preloads [devtools.preload]}}
      :hsl-dev {
        :source-paths ["src/cljs-hsl"]
        :figwheel true
        :compiler {
          :main       danuraisite.hslapp
          :asset-path "/js/compiled/out-hsl"
          :output-to  "resources/public/js/compiled/hsl-app.js"
          :output-dir "resources/public/js/compiled/out-hsl"
          :preloads [devtools.preload]}}
      :don-dev {
        :source-paths ["src/cljs-don"]
        :figwheel true
        :compiler {
          :main       danuraisite.core
          :asset-path "/js/compiled/out-don"
          :output-to  "resources/public/js/compiled/don-app.js"
          :output-dir "resources/public/js/compiled/out-don"
          :preloads [devtools.preload]}}
      :mwl-dev {
        :source-paths ["src/cljs-mwl"]
        :figwheel true
        :compiler {
          :main       danuraisite.mwlapp
          :asset-path "/js/compiled/out-mwl"
          :output-to  "resources/public/js/compiled/mwl-app.js"
          :output-dir "resources/public/js/compiled/out-mwl"
          :preloads [devtools.preload]}}
      :nrf-dev {
        :source-paths ["src/cljs-nrf"]
        :figwheel true
        :compiler {
          :main       danuraisite.nrfapp
          :asset-path "/js/compiled/out-nrf"
          :output-to  "resources/public/js/compiled/nrf-app.js"
          :output-dir "resources/public/js/compiled/out-nrf"
          :preloads [devtools.preload]}}
      :kasei-dev {
        :source-paths ["src/cljs-kasei"]
        :figwheel true
        :compiler {
          :main       danuraisite.core
          :asset-path "/js/compiled/out-kasei"
          :output-to  "resources/public/js/compiled/kasei-app.js"
          :output-dir "resources/public/js/compiled/out-kasei"
          :preloads [devtools.preload]}}
      :lugs-prod {
        :source-paths ["src/cljs-lugs"]
        :compiler {
          :main      danuraisite.lugscore
          :output-to "resources/public/js/compiled/lugs-app.js"
          :output-dir "resources/public/js/compiled/prd-out-lugs"
          :optimizations :advanced :pretty-print false}}
      :hsl-prod {
        :source-paths ["src/cljs-hsl"]
        :compiler {
          :main      danuraisite.hslapp
          :output-to "resources/public/js/compiled/hsl-app.js"
          :output-dir "resources/public/js/compiled/prd-out-hsl"
          :optimizations :advanced :pretty-print false}}
      :don-prod {
        :source-paths ["src/cljs-don"]
        :compiler {
          :main      danuraisite.core
          :output-to "resources/public/js/compiled/don-app.js"
          :output-dir "resources/public/js/compiled/prd-out-don"
          :optimizations :advanced :pretty-print false}}
      :mwl-prod {
        :source-paths ["src/cljs-mwl"]
        :compiler {
          :main      danuraisite.mwlapp
          :output-to "resources/public/js/compiled/mwl-app.js"
          :output-dir "resources/public/js/compiled/prd-out-mwl"
          :optimizations :advanced :pretty-print false}}
      :nrf-prod {
        :source-paths ["src/cljs-nrf"]
        :compiler {
          :main      danuraisite.nrfapp
          :output-to "resources/public/js/compiled/nrf-app.js"
          :output-dir "resources/public/js/compiled/prd-out-nrf"
          :optimizations :advanced :pretty-print false}}
      :kasei-prod {
        :source-paths ["src/cljs-kasei"]
        :compiler {
          :main      danuraisite.core
          :output-to "resources/public/js/compiled/kasei-app.js"
          :output-dir "resources/public/js/compiled/prd-out-kasei"
          :optimizations :advanced :pretty-print false}}
          
      :scores-dev {
        :source-paths ["src/cljs-scores"]
        :figwheel true
        :compiler {
          :main       danuraisite.scores
          :asset-path "/js/compiled/out-scores"
          :output-to  "resources/public/js/compiled/scoresapp.js"
          :output-dir "resources/public/js/compiled/out-scores"
          :preloads [devtools.preload]}}
      :scores-prod {
        :source-paths ["src/cljs-scores"]
        :compiler {
          :main      danuraisite.scores
          :output-to "resources/public/js/compiled/scoresapp.js"
          :output-dir "resources/public/js/compiled/prd-out-scores"
          :optimizations :advanced :pretty-print false}}
      }}

  :figwheel { :css-dirs ["resources/public/css"]}

  ;; Setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {
    :uberjar {
      :aot :all
      :source-paths ["src/clj"]
      :prep-tasks ["compile" ["cljsbuild" "once" "lugs-prod"]
                  "compile" ["cljsbuild" "once" "hsl-prod"]
                  "compile" ["cljsbuild" "once" "don-prod"] 
                  "compile" ["cljsbuild" "once" "mwl-prod"] 
                  "compile" ["cljsbuild" "once" "scores-prod"]
                  "compile" ["cljsbuild" "once" "nrf-prod"] ]
    }
    :dev {
      :dependencies [[reloaded.repl "0.2.4"]
                    [expectations "2.2.0-rc3"]
                    [binaryage/devtools "0.9.4"]
                    [figwheel-sidecar "0.5.14"]
                    [cider/piggieback "0.5.2"]]
      ;; need to add dev source path here to get user.clj loaded
      :source-paths ["src/clj" "dev"]
      ;; for CIDER
      ;; :plugins [[cider/cider-nrepl "0.12.0"]]
      :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
      ;; need to add the compliled assets to the :clean-targets
      :clean-targets ^{:protect false} ["resources/public/js/compiled" :target-path]
    }
  }
)
