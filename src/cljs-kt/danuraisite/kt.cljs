(ns danuraisite.kt
  (:require-macros [cljs.core.async.macros :refer [go]])
	(:require 
		[reagent.core :as r]
    [cljs_http.client :as http]
    [cljs.core.async :refer [<!]]))

(def data (atom nil))
(def killteamdata (r/atom nil))

(def range-symbols {
		"1"   [:polygon {:points "8,2 16,15 2,15" :style {:fill "black"}}]
		"2"   [:circle {:cx "50%" :cy "50%" :r "40%" :stroke "black" :stroke-width "2" :fill "transparent"}]
		"3"   [:rect {:x "10%" :y "10%":width "80%" :height "80%" :style {:fill "midnightblue"}}]
		"6"   [:polygon {:points "9,2 16,7 14,15 4,15, 2,7" :style {:fill "darkred"}}]
})

(def range-symbols-html {
		"1"   "<polygon points=\"8,2 16,15 2,15\" style=\"fill: black;\" />"
		"2"   "<circle cx=\"50%\" cy=\"50%\" r=\"40%\" stroke=\"black\" stroke-width=\"2\" fill=\"transparent\" />"
		"3"   "<rect x=\"10%\" y=\"10%\"width=\"80%\" height=\"80%\" style=\"fill: midnightblue;\" />"
		"6"   "<polygon points=\"9,2 16,7 14,15 4,15, 2,7\" style=\"fill: darkred;\" />"
})

(def numbers ["zero" "one" "two" "three" "four" "five" "six" "seven" "eight" "nine" "ten"])

(defn- markup [& args]
		(if (empty? (apply str args)) 
				[:span "-"]
				[:span {:dangerouslySetInnerHTML {:__html
						(-> (apply str args) 
								(clojure.string/replace #"\[\[" "<span class=\"keyword\">")
								(clojure.string/replace #"\]\]" "</span>")
								(clojure.string/replace #"\[(\d)\]" #(str "<svg class=\"range-symbol\">" (get range-symbols-html (-> %1 last)) "</svg>"))
								(clojure.string/replace #"\*(.+?)\*" #(str "<b>" (last %1) "</b>"))
								)}}]))

(defn op-symbol 
		([ sym class ]
				(case sym
						("melee" "combat")  [:i.ra.ra-crossed-swords {:class class}]
						"ranged"  [:i.fa.fa-crosshairs {:class class}]
						"defense" [:i.fa.fa-shield-alt {:class class}]
						"move"    [:i.fas.fa-angle-double-down {:class class}]
						[:i {:class class}]))
		([ sym ]
				(op-symbol sym "")))


(defn- abilityele [ a ] 
		[:div.mb-2 {:key (gensym)}
				[:span.mr-1.me-1 [:b (-> a :name (str (if (:cost a) (str " (" (:cost a) "AP)" )) ":"))]]
				(-> a :text markup)])

(defn weapons-table [ weapons ]
		[:table.table.table-sm.table-striped.table-hover.table-borderless.text-center
				[:thead
						[:tr [:th ""] [:th.text-start "NAME"] [:th "A"] [:th "BS/WS"] [:th "D"] [:th "SR"] [:th "!"] ]]
				[:tbody
						(for [w weapons]
								(if-let [ammo (:ammo w)]
										[:tr {:key (gensym)}
												[:td (-> w :type op-symbol)]
												[:td.text-start {:col-span 6} 
														[:span (:name w) " "]
														[:em "Each time this weapon is selected to make a shooting attack with, select one of the profiles below to use."]]]
										[:tr {:key (gensym)}    
												[:td (-> w :type op-symbol)]
												[:td.text-start (str (if (-> w :type (= "ammo")) "- ") (:name w))]
												[:td (:a w)]
												[:td (str (:bsws w) "+")]
												[:td (str (-> w :d first) "/" (-> w :d last))]
												[:td (markup (clojure.string/join ", " (:sr w)))]
												[:td (markup (clojure.string/join ", " (:i w)))]]))]])

(defn operative [ op ft ktd ]
		(let [stats (:stats op)]
				[:div.my-3 {:key (gensym)}
						[:div.row.border
								[:div.col-sm-8.info-block.mb-2
										[:div.h5.text-center (or (:altname op) (str (:name ft) " [" (:role op) "]"))]
										[:div [:i (:info op)]]]
								[:div.col-sm-4
										[:table.table.table-sm.table-hover.table-borderless.stat-block.text-center.mb-0
												[:thead [:tr [:th "M"] [:th "APL"] [:th "GA"]]]
												[:tbody [:tr 
														[:td [:span (:m stats)] [:svg.range-symbol (range-symbols "2")]]    
														[:td (:apl stats)]    
														[:td (:ga stats)]]]
												[:thead [:tr [:th "DF"] [:th "SV"] [:th "W"]]]
												[:tbody [:tr
														[:td (:df stats)]    
														[:td (str (:sv stats) "+")]    
														[:td (:w stats)]]]]]]
						[:div.row.py-2 (weapons-table (:weapons op))]
						[:div.row {:style {:min-height "5rem"}}
								(let [abilities (:abilities op)]
										[:div.col.mb-3
												[:div.row.bg-silver.mr-1 [:div.h5 "Abilities"]]
												(if (-> abilities count (= 0))
														[:div "-"]
														(for [a abilities] (abilityele a)))])
								(let [uniqueactions (:uniqueactions op)]
										[:div.col.mb-3
												[:div.row.bg-silver.mr-1 [:div.h5 "Unique Actions"]]
												(if (-> uniqueactions count (= 0))
														[:div "-"]
														(for [ua uniqueactions] (abilityele ua)))])]
						[:div.row.labels
								[:div
										(clojure.string/join ", "
												(remove nil?
														(apply conj 
																(:labels ktd)
																(if (and (:leader op) (-> op :role (not= "Leader"))) "LEADER")
																(:name ft)
																[(:role op)])))]
								[:div.skills {}
										[:div.arrow.arrow-up {:class (if (-> op :skills :combat) "active")}
												[:span [:i.ra.fa-lg.ra-crossed-swords]]]
										[:div.arrow.arrow-down {:class (if (-> op :skills :staunch) "active")}
												[:span [:i.fas.fa-lg.fa-shield-alt]]]
										[:div.arrow.arrow-up {:class (if (-> op :skills :marksman) "active")}
												[:span [:i.fas.fa-lg.fa-crosshairs]]]
										[:div.arrow.arrow-down {:class (if (-> op :skills :scout) "active")}
												[:span [:i.fas.fa-lg.fa-angle-double-down]]]]]]))

(defn- orlist [ options ]
		(let [n (-> options count (- 1))]
				(if (= n 0)
						(clojure.string/capitalize (first options))
						(clojure.string/capitalize (str 
								(->> options (take n) (clojure.string/join ", "))
								" or " 
								(last options))))))

(defn optionlist [ options ]
		[:ul
				(for [opt options]
						(if (vector? opt) 
								[:li {:key (gensym)} (orlist opt)]
								(if (map? opt)
										(case (-> opt first key)
												:oneoptionfromeach [:li {:key (gensym)} "One option from each of the following:" (optionlist (-> opt first val))]
												:and [:li {:key (gensym)} (clojure.string/join "; " (-> opt first val))])
								)))])
(defn a-or-an [ noun ]
		(if (contains? #{\A \E \I \O \U} (clojure.string/upper-case (first noun)))
		"An"
		"A"
	))

(defn- opname [ ft op ]
		(or (:altname op)
				(str (:name ft) " " (:role op))))

(defn- oplistitem [ op ft ]
	[:li {:key (gensym)} 
		(str (:name ft) " " (:role op))
		(if-let [options (-> op :equipment :options)] 
				[:span.options (str
						(if (and (-> op :leader nil?) (-> op :ktlimit nil?)) " each separately")
						" equipped with "
						(if-let [base (-> op :equipment :base)] (clojure.string/lower-case (str (clojure.string/join ", " base) " and ")))
						"one of the following options:") 
						(optionlist options)])])

(defn fireteamroles [ ft ftcount ]
		(if (= 1 ftcount)
			[:div
				[:div (apply markup
								"It also includes " 
								(- (:oplimit ft) 1)
								" [[" (:name ft) " "
								(if (-> ft :operatives count (< 3))
									(-> ft :operatives first :role))
								"]] operatives"
								(if (-> ft :operatives count (> 2))
									" selected from the following list:"
									"."))]
					(when (-> ft :operatives count (> 2))
							(for [op (->> ft :operatives (remove :leader))] [oplistitem op ft])
							)]
			(if (-> ft :operatives count (< 3))
					[:div.mb-3 (markup (-> ft :name a-or-an) " [[" (:name ft) "]] fire team includes " (-> ft :oplimit numbers) " [[" (->> ft :operatives (filter :base) first (opname ft)) "]] operatives.")]
					[:div 
							[:div (markup "A [[" (:name ft) "]] fire team includes " (-> ft :oplimit numbers) " [[" (:name ft) "]] operatives selected from the following list:")]
							[:ul.operatives
									(for [op (->> ft :operatives (remove :leader))] [oplistitem op ft])]])))

(defn teamlimits [ ft ]
		(let [base (->> ft :operatives (filter :base) first)
					req  (->> ft :operatives (filter :reqrole))
					ktlimit (->> ft :operatives (map :ktlimit) (remove nil?) count (< 0))
					ftlimit (->> ft :operatives (map :ftlimit) (remove nil?) count (< 0))
					]
				[:div.mb-3 
						[:span.me-1 (markup "Other than [[" (opname ft base) "]] operatives, your " (if ftlimit "fire" (if ktlimit "kill")) " team can only include each operative above once.")]
						(for [r req]
								[:span {:key (gensym)} (markup "Your kill team can only include a [[" (opname ft r) "]] operative if it also includes one [[" (opname ft (->> ft :operatives (filter #(= (:role %) (:reqrole r))) first)) "]] operative." )])
				]))
(defn fireteamleader [ ft ftcount ]
		(let [base (->> ft :operatives (filter :base) first)
					leader (->> ft :operatives (filter :leader) first)]
				[:div.mb-3 
						[:div (markup "If your kill team does not include any other [[leader]] operatives, instead of selecting one [[" 
														(opname ft base) "]] operative for one [[" (:name ft) "]] fire team, you can select one [[" (opname ft leader) "]] operative"
														(if (-> leader :equipment :options)
																(str " equipped with "
																		(if-let [base (-> leader :equipment :base)] (clojure.string/lower-case (str (clojure.string/join ", " base) " and ")))
																		"one of the following options:")))]
						(if-let [options (-> leader :equipment :options)] (optionlist options))]))

(defn fireteaminfo [ ft ktd ]
	(let [ftcount (:fireteamcount ktd)]
		[:div.mb-3
				(fireteamroles ft ftcount)
				(fireteamleader ft ftcount)]))


(defn fireteam [ ft ktd ]
		[:div {:key (gensym)}
				[:div.h2 (str "Archetype: " (->> ft :archetypes (clojure.string/join " / ")))]
				[:div.h3 (str (:name ft) " Fire Team")]
				(fireteaminfo ft ktd)])

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



(defn equipmentitem [ e ]
		[:div.mb-3.bg-light.m-2.equipment {:key (gensym)}
				[:div.fs-5 {:style {:font-family "courier"}} (str (:name e) " [" (:cost e) "EP]")] 
				[:div.mb-1 (markup
						(if-let [restriction (:restriction e)] 
								(str (orlist (map #(str "[[" % "]]") restriction)) " only. "))
						(if-let [type (:type e)]
								(str "The operative gains the following " 
										(get {"melee" "melee attack" "ranged" "ranged attack" "ability" "ability"} type)
										 " for the battle:")
								(:text e)))]
				(case (:type e)
						("melee" "ranged")
								(let [w (:weapon e)]
										[:div 
												[:table.table.table-sm
														[:thead [:tr [:th "Name"] [:th "A"] [:th (if (-> e :type (= "melee")) "WS" "BS")] [:th "D"] ]]
														[:tbody [:tr [:td (:name w)] [:td (:a w)] [:td (str (:bsws w) "+")] [:td (str (-> w :d first) "/" (-> w :d last))]]]]
												(if-let [sr (:sr w)]
														[:table.table.table-sm
																[:thead [:tr [:th "Special Rules"]]]
																[:tbody [:tr [:td (->> sr (clojure.string/join ", ") markup)]]]])
												(if-let [i (:i w)]
														[:table.table.table-sm
																[:thead [:tr [:th "i"]]]
																[:tbody [:tr [:td (->> i (clojure.string/join ", ") markup)]]]])])
						"ability" (abilityele (assoc (:ability e) :name (:name e)))
						nil)
				])

(defn equipmentcontainer [ ktd ]
		(let [{:keys [:equipmentexclusions equipment]} ktd]
				[:div.row
						[:div.h3 "Equipment"]
						[:div.mb-3 (markup "[[" (:name ktd) "]] operatives")]
						[:div {:style {:column-count 2}} 
								(for [equip equipment]
										(equipmentitem equip))]]))

(defn killteamcontainer [ kt ]
		[:div 
				(if-let [ftc (:fireteamcount kt)]
						[:div 
								[:div.h1 (-> kt :killteamname (str " Kill Team"))]
								[:div (markup (-> kt :killteamname a-or-an) " [[" (:killteamname kt) "]] kill team consists of " (numbers ftc ftc) " fire teams selected from the following list:")]
								[:ul.fireteams 
										(for [ft (:fireteams kt)]
												[:li {:key (gensym)} (:name ft)])]
								])
				;; Fire Teams
				[:div.mb-3 (for [ ft (:fireteams kt)] (fireteam ft (dissoc kt :fireteams)))]
				[:div.mb-3 (for [ ft (:fireteams kt)] (killteamoperatives ft (dissoc kt :fireteams)))]
				;; Ploys
				(ploycontainer (:ploys kt))
				(equipmentcontainer (dissoc kt :fireteams))
				])

(defn page []
	@data
	@killteamdata
	[:div.container
			[:select.h0.w-100.mb-3 {:on-change (fn [ele] (-> ele .-target .-value prn) (reset! killteamdata (->> @data (filter #(= (:id %) (-> ele .-target .-value))) first)))}
					(for [kt @data] [:option {:key (:id kt) :value (:id kt)} (:name kt)])]
			[killteamcontainer @killteamdata]
			])

(go (let [response (<! (http/get "/api/data/kt2data"))]
	(reset! data (:body response))

	(reset! killteamdata (first @data))
	(r/render [page] (.getElementById js/document "app")))) 