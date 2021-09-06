(ns danuraisite.kt
    (:require 
      [reagent.core :as r]
      [danuraisite.ktdata :refer [data]]))

(def killteamdata (r/atom (first data)))

(def range-symbols {
    1   [:polygon {:points "8,2 16,15 2,15" :style {:fill "black"}}]
    2   [:circle {:cx "50%" :cy "50%" :r "40%" :stroke "black" :stroke-width "2" :fill "transparent"}]
    3   [:rect {:x "10%" :y "10%":width "80%" :height "80%" :style {:fill "midnightblue"}}]
    6   [:polygon {:points "9,2 16,7 14,15 4,15, 2,7" :style {:fill "darkred"}}]
})

(def range-symbols-html {
    1   "<polygon points=\"8,2 16,15 2,15\" style=\"fill: black;\" />"
    2   "<circle cx=\"50%\" cy=\"50%\" r=\"40%\" stroke=\"black\" stroke-width=\"2\" fill=\"transparent\" />"
    3   "<rect x=\"10%\" y=\"10%\"width=\"80%\" height=\"80%\" style=\"fill: midnightblue;\" />"
    6   "<polygon points=\"9,2 16,7 14,15 4,15, 2,7\" style=\"fill: darkred;\" />"
})

(def numbers ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine" "ten"])

(defn- markup [& args]
        [:span {:dangerouslySetInnerHTML {:__html
            (-> (apply str args) 
                (clojure.string/replace #"\[\[" "<span class=\"keyword\">")
                (clojure.string/replace #"\]\]" "</span>")
                (clojure.string/replace #"\[(\d)\]" #(str "<svg class=\"range-symbol\">" (get range-symbols-html (-> %1 last cljs.reader/read-string)) "</svg>"))
                (clojure.string/replace #"\*(.+?)\*" #(str "<b>" (last %1) "</b>"))
                )}}])

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
                    [:div.h5.text-center (or (:altname op) (str (:name ft) " [" (:role op) "]"))]
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
                        [:div.mb-2 {:key (gensym)}
                            [:span.mr-1.me-1 [:b (-> a :name (str ":"))]]
                            (-> a :text markup)])]
                [:div.col-sm-6
                    [:div.row.bg-silver [:div.h5 "Unique Actions"]]
                    (for [a (:uniqueactions op)] 
                        [:div {:key (gensym)}
                            [:span.mr-1.me-1 [:b (-> a :name (str (if (:cost a) (str " (" (:cost a) "AP)" )) ":"))]]
                            (-> a :text markup)])]]
            [:div.row.labels
                [:div
                    (clojure.string/join ", "
                        (remove nil?
                            (apply conj 
                                (:labels ktd)
                                (if (and (:leader op) (-> op :name (not= "LEADER"))) "LEADER")
                                (:name ft)
                                [(:role op)])))]
                [:div.skills {}
                    [:div.arrow.arrow-up {:class (if (-> op :skills :combat) "active")}
                        [:span [:i.ra.fa-lg.ra-crossed-swords]]]
                    [:div.arrow.arrow-down {:class (if (-> op :skills :defense) "active")}
                        [:span [:i.fas.fa-lg.fa-shield-alt]]]
                    [:div.arrow.arrow-up {:class (if (-> op :skills :ranged) "active")}
                        [:span [:i.fas.fa-lg.fa-crosshairs]]]
                    [:div.arrow.arrow-down {:class (if (-> op :skills :move) "active")}
                        [:span [:i.fas.fa-lg.fa-angle-double-down]]]]]]))

(defn- optionlist [ options ]
    (let [n (-> options count (- 1))]
        (clojure.string/capitalize (str 
            (->> options (take n) (clojure.string/join ", "))
            " or " 
            (last options)
            "."))))
(defn- opname [ ft op ]
    (or (:altname op)
        (str (:name ft) " " (:role op))))
(defn fireteamroles [ ft ]
    (if (-> ft :operatives count (< 3))
        [:div.mb-3 (markup "A [[" (:name ft) "]] fire team includes " (-> ft :oplimit numbers) " [[" (->> ft :operatives (filter :base) first (opname ft)) "]] operatives.")]
        [:div 
            [:div (markup "A [[" (:name ft) "]] fire team includes " (-> ft :oplimit numbers) " [[" (:name ft) "]] operatives selected from the following list:")]
            [:ul.operatives
                (for [op (->> ft :operatives (remove :leader))]
                    [:li {:key (gensym)} (str (:name ft) " " (:role op))
                        (if-let [options (-> op :equipment :options)] 
                            [:span.options (str
                                (if (and (-> op :leader nil?) (-> op :ktlimit nil?)) " each separately")
                                " equipped with "
                                (if-let [base (-> op :equipment :base)] (clojure.string/lower-case (str (clojure.string/join ", " base) " and ")))
                                "one of the following options:") 
                                [:ul [:li (optionlist options)]]])])]]))

(defn fireteamlimits [ ft ]
    (let [base (->> ft :operatives (filter :base) first)
          req  (->> ft :operatives (filter :reqrole))]
        [:div.mb-3 
            [:span.me-1 (markup "Other than [[" (opname ft base) "]] operatives, your kill team can only include each operative above once.")]
            (for [r req]
                [:span {:key (gensym)} (markup "Your kill team can only include a [[" (opname ft r) "]] operative if it also includes one [[" (opname ft (->> ft :operatives (filter #(= (:role %) (:reqrole r))) first)) "]] operative." )])
        ]))
(defn fireteamleader [ ft ]
    (let [base (->> ft :operatives (filter :base) first)
          leader (->> ft :operatives (filter :leader) first)]
        [:div.mb-3 (markup "If your kill team does not include any other [[leader]] operatives, instead of selecting one [[" (opname ft base) "]] operative for one [[" (:name ft) "]] fire team, you can select one [[" (opname ft leader) "]] operative.")]))
(defn fireteaminfo [ ft ]
    [:div.mb-3
        (fireteamroles ft)
        (if (-> ft :operatives count (> 2)) (fireteamlimits ft))
        (fireteamleader ft)])


(defn fireteam [ ft ]; ktd ]
    [:div {:key (gensym)}
        [:div.h2 (str "Archetype: " (->> ft :archetypes (clojure.string/join " / ")))]
        [:div.h3 (str (:name ft) " Fire Team")]
        (fireteaminfo ft)])

(defn killteamoperatives [ ft ktd ]
    [:div.py-3 {:key (gensym)}
        (for [op (:operatives ft)]
            (operative op ft ktd))])

(defn- ployelement [ ploy ]
    [:div.mb-3 {:key (gensym)}
        [:div.h0.d-flex.justify-content-between
            [:div (:name ploy)]
            [:div.me-1 (str (:cp ploy) "CP")]]
        [:div (-> ploy :text markup)]])

(defn ploycontainer [ {:keys [strategic tactical]} ]
    [:div.row
        (map 
            (fn [[name ploys]]
                [:div.col-sm-6 {:key (gensym)}
                    [:div.h3 name]
                    (for [ sp ploys ] (ployelement sp) )])
            [["Strategic Ploys" strategic] ["Tactical Ploys" tactical]])])

(defn killteamcontainer [ kt ]
    [:div 
        (if-let [ftc (:fireteamcount kt)]
            [:div 
                [:div.h1 (-> kt :killteamname (str " Kill Team"))]
                [:div (markup "A [[" (:killteamname kt) "]] kill team consists of " (numbers ftc ftc) " fire teams selected from the following list:")]
                [:ul.fireteams 
                    (for [ft (:fireteams kt)]
                        [:li {:key (gensym)} (:name ft)])]
                ])
        ;; Fire Teams
        [:div.mb-3 (for [ ft (:fireteams kt)] (fireteam ft))] ; (dissoc kt :fireteams)))]
        [:div.mb-3 (for [ ft (:fireteams kt)] (killteamoperatives ft (dissoc kt :fireteams)))]
        ;; Ploys
        (ploycontainer (:ploys kt))
        ;; Equipment
        ])

(defn page []
    [:div.container
        [:select.h0.w-100.mb-3 {:on-change (fn [ele] (reset! killteamdata (->> data (filter #(= (:id %) (-> ele .-target .-value cljs.reader/read-string))) first)))}
            (for [kt data] [:option {:key (:id kt) :value (:id kt)} (:name kt)])]
        [killteamcontainer @killteamdata]])

(r/render [page] (.getElementById js/document "app"))