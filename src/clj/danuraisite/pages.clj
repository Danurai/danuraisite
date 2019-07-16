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

(def decks ["_wED9DexgA==" "_wEB1ARxhwHFBvmOAcaZQJQDAQwWlwMwMXSl"])

(defn aosc-tools [ req ]
;; Parse Decklist
;; Custom API link
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-2
        [:div.row
          [:div.col-sm-6
            [:div.row
              [:h4 "Parse Decklist examples"]
              (for [deck decks]
                ^{:key (gensym)}[:div [:b.mr-1 deck] [:div (-> deck model/parsedeck json/write-str)]])]]
          [:div.col-sm-6
            [:form {:method "post" :action "/aosc/customsource"}
              [:div.form-group
                [:label {:for "#url"} "Source URL"]
                [:input.form-control {:type "text" :placeholder "http://" :name "url"}]]
              [:button.btn.btn-primary {:type "submit"} "Go"]]]]]]))
        
        