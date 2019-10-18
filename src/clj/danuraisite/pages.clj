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
(load "pages/netrunner")

(defn homepage [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-3
        [:div.row
          [:div.col
            [:h5 "Legends Untold: The Great Sewer (LUGS)"]
            [:div.mb-2 "Character Builder and Icon Reference"]
            [:h5 "Age of Sigmar: Champions (AoS:C)"]
            [:div.mb-2 "Card list, tooltip demo and API links"]
            [:h5 "Colours"]
            [:div.mb-2 [:b "H"] "ue " [:b "S"] "aturation and " [:b "L"] "uminance Demo and Citadel Paint comparison chart"]
            [:h5 "Dead of Night (DoN)"]
            [:div.mb-2 "Victim roll..."]
            [:h5 "Netrunner"]
            [:div.mb-2 "Rotation Checker and Folder planner"]
          ]]]]))
      