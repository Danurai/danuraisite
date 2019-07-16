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

(defn homepage [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-2 "Homepage"]]))
      
(load "pages/legendsuntold")
(load "pages/hsl")
(load "pages/deadofnight")
(load "pages/ageofsigmarchampions")
