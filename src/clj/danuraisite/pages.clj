(ns danuraisite.pages
  (:require 
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [hiccup.page :as h]
    [cemerick.friend :as friend]
    [clj-time.coerce :as tc]
    [clj-time.format :as tf]
    [danuraisite.database :as db]
    [danuraisite.model :as model]))

(load "pages/common")      
(load "pages/admin")
(load "pages/legendsuntold")
(load "pages/colour")
(load "pages/deadofnight")
(load "pages/netrunner")
(load "pages/kt2")
;(load "pages/apps")

(def custom-formatter (tf/formatter "dd/MMM/yyyy"))

(defn homepage [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-3
        [:div.row
          [:div.col
            [:h5 "Legends Untold: The Great Sewer (LUGS)"]
            [:div.mb-2 "Character / Party Builder and Icon Reference"]
            [:h5 "Colours"]
            [:div.mb-2 [:b "H"] "ue " [:b "S"] "aturation and " [:b "L"] "uminance demo and Paint comparison chart"]
            [:h5 "Dead of Night (DoN)"]
            [:div.mb-2 "Victim roll..."]
            [:h5 "Netrunner"]
            [:div.mb-2 "Rotation Checker, Most Wanted List (MWL) and Folder planner"]
            [:h5 "Scores"]
            [:div.mb-2 "Spirit Island score log"]
            [:h5 "Kill Team"]
            [:div.mb-2 "Sample Compendium Reference and Spec Ops Dataslates"]
            [:h5 "Bardsung"]
            [:div.mb-2 "Marching Order Randomiser"]
          ]]]]))
      
(defn scorehome [ req ]
  (h/html5 
    pretty-head
    [:body
      (navbar req)
      [:div#mapmodal.modal.fade {:tabindex -1}
        [:div.modal-dialog.modal-lg
          [:div.modal-content
            [:div.modal-header
              [:h5 "Scenario Map"]
              [:button.btn-close {:type "button" :data-bs-dismiss "modal"}]]
            [:div.modal-body
              [:canvas#drawing.border.mx-auto {:width "700px" :height "450px"}]]]]]
      [:div#app
        [:div.ms-3 "loading..."]]
      [:div.container-fluid.mb-3
        [:div.row 
          [:table.table.table-hover
            [:thead 
              [:tr
                [:th.text-center "Del"]
                [:th "Date"]
                [:th "Scenario"]
                [:th "Adversary"]
                [:th "Diff."]
                [:th "Spirits"]
                [:th.text-center "Win?"]
                [:th.text-center "Inv. Deck"]
                [:th.text-center "Dahan"]
                [:th.text-center "Blight"]
                [:th.text-center "Score"]]]
            [:tbody
              (for [gm (db/getsiscores)]
                [:tr 
                  [:td.text-center [:button.btn.btn-sm.btn-danger {
                    ;:on-click (if (.confirm js/window "Are you sure you want to delete this game log") )
                    :title "Delete?"} 
                    [:i.fas.fa-times-circle]]]
                  [:td (tf/unparse custom-formatter (-> gm :date tc/from-long))]
                  [:td (:scenario gm)]
                  [:td (if (= "none" (:adversary gm)) "(None)" (str (:adversary gm) " (" (:advlvl gm) ")"))]
                  [:td (:difficulty gm)]
                  [:td (:spirits gm)]
                  [:td.text-center (if (:win gm) [:i.fas.fa-check-circle.text-success][:i.fas.fa-times-circle.txt-danger])]
                  [:td.text-center (:invdeck gm)]
                  [:td.text-center (:dahan gm)]
                  [:td.text-center (:blight gm)]
                  [:td.text-center (:score gm)]
                ]
              )]]]]
      (h/include-js "/js/compiled/scoresapp.js")]))