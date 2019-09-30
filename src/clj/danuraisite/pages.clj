(ns danuraisite.pages
  (:require 
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [hiccup.page :as h]
    [cemerick.friend :as friend]
    [danuraisite.database :as db]
    [danuraisite.model :as model]))

(load "pages/common")        
(load "pages/admin")
(load "pages/legendsuntold")
(load "pages/colour")
(load "pages/deadofnight")
(load "pages/ageofsigmarchampions")
(load "pages/netrunner")

(defn kaseipage [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div#kasei]
      (h/include-js "js/compiled/kasei-app.js")]))

(defn homepage [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-2 "Homepage"]]))
      