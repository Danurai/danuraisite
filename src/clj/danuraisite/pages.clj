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
;(load "pages/apps")

(defn scorehome [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div#app]
      (h/include-js "/js/compiled/scoresapp.js")]))

(defn homepage [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-3
        [:div.row
          [:div.col
            [:h5 "Legends Untold"]
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
      
(defn painttbl [ req ]
	(let [cols (-> "private/paintlist.json" io/resource slurp (json/read-str :key-fn keyword))]
		(h/html5
			pretty-head
			[:div.container.my-3
				[:table#tbl.table
					[:thead [:tr [:th "Name"][:th "Range"][:th "OldGW"][:th "Hex"]]]
					[:tbody
						(for [clr cols]
							[:tr [:td (:name clr)][:td (:range clr)][:td (:oldgw clr)][:td (:hex clr)]])]
				]]
			[:script "$(document).ready(function(){$('#tbl').DataTable({paging: false})});"]
			(h/include-css "//cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css")
			(h/include-js "//cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"))))