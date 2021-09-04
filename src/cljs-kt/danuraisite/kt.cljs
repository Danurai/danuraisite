(ns danuraisite.kt
    (:require 
      [reagent.core :as r]
      [danuraisite.ktdata :refer [data]]))

(def range-symbols {
    1   [:polygon {:points "8,2 16,15 2,15" :style {:fill "black"}}]
    2   [:circle {:cx "50%" :cy "50%" :r "40%" :stroke "black" :stroke-width "2" :fill "transparent"}]
    3   [:rect {:x "10%" :y "10%":width "80%" :height "80%" :style {:fill "midnightblue"}}]
    6   [:polygon {:points "9,2 16,7 14,15 4,15, 2,7" :style {:fill "darkred"}}]
})

(defn op-symbol 
    ([ sym class ]
        (case sym
            "combat"  [:i.ra.ra-crossed-swords {:class class}]
            "ranged"  [:i.fa.fa-crosshairs {:class class}]
            "defense" [:i.fa.fa-shield-alt {:class class}]
            "move"    [:i.fas.fa-angle-double-down {:class class}]
            [:i {:class class}]))
    ([ sym ]
        (op-symbol sym "")))

(defn operative [ op ft ktd ]
    (let [stats (:stats op)]
        [:div.mb-2 {:key (gensym)}
            [:div.row.border
                [:div.col-sm-8.info-block.mb-2
                    [:div.h5.text-center (:role op)]
                    [:div [:i (:info op)]]]
                [:div.col-sm-4
                    [:table.table.table-sm.table-hover.table-borderless.stat-block.text-center.mb-0
                        [:thead [:tr [:th "M"] [:th "APL"] [:th "GA"]]]
                        [:tbody [:tr 
                            [:td [:span (-> stats :m first)] [:svg.range-symbol (-> stats :m last range-symbols)]]    
                            [:td (:apl stats)]    
                            [:td (:ga stats)]]]
                        [:thead [:tr [:th "DF"] [:th "SV"] [:th "W"]]]
                        [:tbody [:tr
                            [:td (:df stats)]    
                            [:td (str (:sv stats) "+")]    
                            [:td (:w stats)]]]]]]
            [:div.row.py-2
                [:table.table.table-sm.table-striped.table-hover.table-borderless.text-center
                    [:thead
                        [:tr [:th ""] [:th.text-start "NAME"] [:th "A"] [:th "BS/WS"] [:th "D"] [:th "SA"] [:th "I"] ]]
                    [:tbody
                        (for [w (:weapons op)]
                            [:tr {:key (gensym)}
                                [:td (-> w :type op-symbol)]
                                [:td.text-start (:name w)]
                                [:td (:a w)]
                                [:td (str (:bsws w) "+")]
                                [:td (str (-> w :d first) "/" (-> w :d last))]
                                [:td (clojure.string/join "," (:sa w))]
                                [:td (clojure.string/join "," (:i w))]])]]]
            [:div.row {:style {:min-height "5rem"}}
                [:div.col-sm-6.mb-3
                    [:div.row.bg-silver.mr-1 [:div.h5 "Abilities"]]
                    (for [a (:abilities op)] 
                        [:div.row {:key (gensym)}
                            [:span.mr-1.me-1 [:b (-> a :name (str ":"))]]
                            [:span (:text a)]])]
                [:div.col-sm-6
                    [:div.row.bg-silver [:div.h5 "Unique Actions"]]
                    (for [a (:uniqueactions op)] 
                        [:div {:key (gensym)}
                            [:span.mr-1.me-1 [:b (-> a :name (str ":"))]]
                            [:span (:text a)]])]]
            [:div.row.labels
                [:div
                    (clojure.string/join ", "
                        (remove nil?
                            (apply conj 
                                (:labels ktd)
                                (if (:leader op) "LEADER")
                                (:labels op))))]
                [:div {:style {:position "absolute" :bottom 0 :right 0 :width "auto"}}
                    (for [s (-> op :skills keys)]
                        [:div.d-inline-block.p-1.mr-1.me-1.border
                            {:key (gensym) :class (if (-> op :skills s) "active-skill" "bg-white")}   
                            (-> s name (op-symbol "fa-2x"))])]]
        ]))

(defn fireteam [ ft ktd ]
    [:div {:key (gensym)}
        [:div.h2 (str "Archetype: " (->> ft :archetypes (clojure.string/join " / ")))]
        [:div.h3 (str (:name ft) " Fire Team")]
        (for [op (:operatives ft)]
            (operative op ft ktd))])

(defn ployelement [ ploy ]
    [:div.mb-3 {:key (gensym)}
        [:div.h0.d-flex.justify-content-between
            [:div (:name ploy)]
            [:div.me-1 (str (:cp ploy) "CP")]]
        [:div (:text ploy)]])

(defn ploycontainer [ {:keys [strategic tactical]} ]
    [:div.row
        (map 
            (fn [[name ploys]]
                [:div.col-sm-6 {:key (gensym)}
                    [:div.h3 name]
                    (for [ sp ploys ] (ployelement sp) )])
            [["Strategic Ploys" strategic] ["Tactical Ploys" tactical]])])

(def numbers ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine" "ten"])
(defn killteamcontainer [ kt ]
    [:div 
        [:div.h0 (:name kt)]
        [:div.h1 (-> kt :killteamname (str " Kill Team"))]
        (if-let [ftc (:fireteamcount kt)]
            [:div 
                [:div (str "A [[" (:killteamname kt) "]] kill team consists of " (numbers ftc ftc) " fire teams selected from the following list:")]
                [:ul.fireteams 
                    (for [ft (:fireteams kt)]
                        [:li {:key (gensym)} (:name ft)])]
                ])
        ;; Fire Teams
        [:div.mb-3 (for [ ft (:fireteams kt)] (fireteam ft (dissoc kt :fireteams)))]
        ;; Ploys
        (ploycontainer (:ploys kt))
        ;; Equipment
        ])

(defn page []
    [:div.container
        [killteamcontainer (first data)]])

(r/render [page] (.getElementById js/document "app"))