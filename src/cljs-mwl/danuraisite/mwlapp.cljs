(ns danuraisite.mwlapp
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    ;[goog.string :as gstring]
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    ))
    
(enable-console-print!)

(def cycles (r/atom nil))
(def mwls (r/atom nil))
(def packs (r/atom nil))
(def cardlist (r/atom nil))
(def cards (r/atom nil))
(def factions (r/atom nil))
(def colours (r/atom nil))
(def types (r/atom nil))

(def rotations [
  {:id 1 :cycle 20 :date "1-10-2018" :cycles #{1 2 4}}
  {:id 2 :cycle 26 :date "27-12-2019" :cycles #{1 2 3 4 6 13 20}}])


(defn normalise [ name ]
  (-> name
      (clojure.string/lower-case)
      (clojure.string/replace #"[\u00e0-\u00e5]" "a")
      (clojure.string/replace #"[\u00e8-\u00eb]" "e")
      (clojure.string/replace #"[\u00ec-\u00ef]" "i")
      (clojure.string/replace #"[\u00f2-\u00f6]" "o")
      (clojure.string/replace #"[\u00f9-\u00fc]" "u")
      (clojure.string/replace #"[\u015e-\u015f]" "s") ;Ş
      (clojure.string/replace #"[\u0022|\s|\-|\_]" "")))

(def nrdb-url "https://netrunnerdb.com/api/2.0/public/")

;(defn json-callback [ atm, data ]
;  (reset! atm (-> data (js->clj :keywordize-keys true) :data)))
;
;(defn- colour-callback [ data ]
;  (reset! colours 
;    (apply merge 
;      (map 
;        #(hash-map (:code %) (str "#" (:color %))) 
;        (json-callback factions data)))))
;
;(defn- cards-callback [ data ]
;  (let [cardsapi  (-> data (js->clj :keywordize-keys true) :data)
;        cardsslug (->> cardsapi (map #(assoc % :slug (-> % :title normalise))))]
;    (reset! cards    cardsslug)
;    (reset! cardlist (filter #(= (:pack_code %) "core") cardsslug))))

(defn- init! []
  (go
    (reset! colours 
      (apply merge 
        (map 
          #(hash-map (:code %) (str "#" (:color %))) 
          (-> (<! (http/get (str nrdb-url "factions") {:with-credentials? false})) :body :data))))
    (reset! cycles   (-> (<! (http/get (str nrdb-url "cycles")   {:with-credentials? false})) :body :data))
    (reset! packs    (-> (<! (http/get (str nrdb-url "packs")    {:with-credentials? false})) :body :data))
    (reset! mwls     (-> (<! (http/get (str nrdb-url "mwl")      {:with-credentials? false})) :body :data))
    (reset! types    (-> (<! (http/get (str nrdb-url "types")    {:with-credentials? false})) :body :data))
    (reset! factions (-> (<! (http/get (str nrdb-url "factions") {:with-credentials? false})) :body :data))
    (let [cardsapi (<! (http/get (str nrdb-url "cards") {:with-credentials? false}))
          cardsslug (->> cardsapi :body :data (map #(assoc % :slug (-> % :title normalise))))]
      (reset! cards    cardsslug)
      (reset! cardlist (filter #(= (:pack_code %) "core") cardsslug)))))
          
  
      
(defn- packs-in-cycle [ packs cycle_code ]
  (->> packs
      (filter #(= (:cycle_code %) cycle_code))
      (sort-by :position)))
      
(defn- parsedeck! [ decklist cards clist sname srot? ]
  (let [lines (re-seq #".+" decklist)]
    (prn (second lines))
    (reset! sname (first lines))
    (reset! srot? false)
    (reset! clist 
      (apply conj 
        [(-> (filter #(= (:title %) (second lines)) cards)
            first
            (assoc :quantity 1))]
        (mapv (fn [[a b c d e]]
          (-> (filter #(= (:title %) (if d d e)) cards)
              first
              (assoc :quantity b)))
          (re-seq #"([0-9])x\s((.+?)\s\u25CF|(.+))" decklist))))))
          
            
(defn- isrotated? [ cardcycles ]
  (= (count cardcycles) (->> cardcycles (filter :rotated) count)))
    
(defn- packtags [ cardcycles ]
  (for [oc cardcycles]
    ^{:key (gensym)}[:i.icon.me-2 {
      :class (str "icon-" (:code oc) (if (:rotated oc) " text-danger")) 
      :title (str (:name oc) (if (:rotated oc) " !Rotated")) }]))              
        
(defn- card-div [ colours cards packs cycles mwl c ]
  (let [cardmwl        (-> mwl (get (-> c :code str keyword)))
        cardpackcodes  (->> cards (filter #(= (:title %) (:title c))) (map :pack_code))
        cardcyclecodes (->> packs (filter #(some #{(:code %)} cardpackcodes)) (map :cycle_code))
        cardcycles     (filter #(some #{(:code %)} cardcyclecodes) cycles)
        rotated?       (isrotated? cardcycles)]
    ^{:key (gensym)}[:div 
      [:span
        [:a.cardlink {
          :data-code (:code c) 
          :href "#" 
          :on-click #(.preventDefault %) 
          :data-image_url (:image_url c)
          :style {
            :color (get colours (:faction_code c))
            :text-decoration (if rotated? "line-through")
          }}
          (if (:is_restricted cardmwl)
            [:i.fas.fa-exclamation.text-warning.me-2 {:title "Restricted"}])
          (if (= 0 (:deck_limit cardmwl))
            [:i.fas.fa-times-circle.text-danger.me-2 {:title "Removed"}])
          [:span.me-2 (str (if (:uniqueness c) "\u2B24 ") (:title c) " (" (:quantity c) ")")]
          (repeat (:universal_faction_cost cardmwl) ^{:key (gensym)}[:i.fas.fa-circle.me-1.fa-xs])
          (repeat (:global_penalty cardmwl) ^{:key (gensym)}[:i.fas.fa-circle.me-1.fa-xs])]
        (packtags cardcycles)]]))
        
(defn- packlist [ cardset cards cycles packs ]
; Find all matching cards from @cards
; #core +Packs
; #revised core +Packs
; #SC19 + packs
  (let [settitles (->> cardset (map :title) set)
        alltitles (->> cards (filter #(contains? settitles (:title %))))
        packcodes (->> alltitles (map :pack_code) distinct)
        packs     (->> packs (filter #(contains? (set packcodes) (:code %))))]
    [:div 
      [:h5 "Packs Used"]
      (for [p packs]
        [:div {:key (gensym)}
          [:div [:b (:name p)]]
          (for [c (->> alltitles (filter #(= (:pack_code %) (:code p))) (map :title))]
            [:div {:key (gensym)} c])])]))
      ;[:div alltitles]
      ;[:div (clojure.string/join ", " (map :name packs))]
  
(defn- count-qty [ crds ]
  (->> crds
       (map #(-> % :quantity int))
       (apply +)))
        
(defn- main [mwl selected_name selected_rotated?]
  [:div.row
    [:div.col-sm-3 
      [:div.row-fluid
        [:ul.nav.nav-tabs.w-100.nav-fill {:role "tablist"}
          [:li.nav-item
            [:a.nav-link.active {:data-bs-toggle "tab" :href "#packs" :role "tab"} "Packs"]]
          [:li.nav-item
            [:a.nav-link {:data-bs-toggle "tab" :href "#deck" :role "tab"} "Deck"]]]
        [:div.tab-content.w-100.py-2
          [:div#packs.tab-pane.fade.show.active {:role "tabpanel"}
            [:div.list-group.w-100
              (doall (for [c (->> @cycles (sort-by :position) reverse)]
                (let [packs (packs-in-cycle @packs (:code c))
                      rotated_icon (if (:rotated c) [:i.fas.fa-sync-alt.text-danger {:title "Rotated"}])]
                  ^{:key (gensym)}[:div.list-group-item
                    [:div {
                      :style {:cursor "pointer"}
                      :on-click (fn []
                        (reset! cardlist (filter #(some #{(:pack_code %)} (map :code packs)) @cards))
                        (reset! selected_name (:name c))
                        (reset! selected_rotated? (:rotated c)))
                      }
                      [:i.icon.me-2 {:class (str "icon-" (:code c))}]
                      [:span (:name c)]
                      [:span.float-right rotated_icon]]
                    (if (< 1 (:size c))
                      (for [p packs]
                        ^{:key (gensym)}[:div.pl-2 {
                          :style {:cursor "pointer"} 
                          :on-click (fn []
                            (reset! cardlist (filter #(= (:pack_code %) (:code p)) @cards))
                            (reset! selected_name (:name p))
                            (reset! selected_rotated? (:rotated c)))
                          }
                            [:i.icon.icon-subroutine.me-2] 
                            (:name p)
                            [:span.float-right rotated_icon]]))])))]]
          [:div#deck.tab-pane.fade {:role "tabpanel"}
            [:h5.mb-2 "Paste Decklist Below"]
            [:textarea.form-control {
              :rows 25
              :on-input #(parsedeck! (-> % .-target .-value) @cards cardlist selected_name selected_rotated?)}]]]]]
    [:div.col-sm-9
      [:div.pt-2 ;sticky-top.pt-2
        [:div.row-fluid.d-flex.mb-2
          [:span.w-50.h4 
            [:span.me-2 @selected_name]
            [:span.me-2 (str "(" (->> @cardlist count-qty) ")")]
            [:span {:hidden (false? @selected_rotated?)}
              [:span.fas.fa-sync-alt.text-danger {:title "Rotated"}]]]
          [:select.form-control.w-50.ms-auto {:value @mwl :on-change #(reset! mwl (-> % .-target .-value))}
            (for [mwl @mwls]
              ^{:key (gensym)}[:option (:name mwl)])]]
        (let [mwlcards (->> @mwls (filter #(= (:name %) @mwl)) first :cards)
              cardset @cardlist
              typeset (filter #(some #{(:code %)} (->> cardset (map :type_code) distinct)) @types)
              id (filter #(= (:code %) "identity") @types)
              colours @colours
              cards @cards 
              packs @packs
              cycles @cycles]
          (doall (for [sc (->> @cardlist (map :side_code) distinct)]
            [:div.row-fluid.mb-3 {:key (gensym)}
              [:div
                [:b.me-2 (clojure.string/capitalize sc)]
                [:span (str "(" (->> @cardlist (filter #(= (:side_code %) sc)) count-qty) ")")]]
              [:div {:style {
                :-webkit-column-gap "20px" :-webkit-column-count 3
                :-moz-column-gap "20px" :-moz-column-count 3
                :column-gap "20px" :column-count 3 }}
                (for [tc (->> typeset (filter #(= (:side_code %) sc)) (apply merge id) (sort-by :position) (map :code))]
                  ^{:key (gensym)}[:div {:style {:break-inside "avoid"}}
                    [:div 
                      [:u 
                        [:span.me-2 (clojure.string/capitalize tc)]
                        [:span (str "(" (->> cardset (filter #(= (:side_code %) sc)) (filter #(= (:type_code %) tc)) count-qty) ")")]]]
                      (for [c (->> cardset (filter #(= (:side_code %) sc)) (filter #(= (:type_code %) tc)) (sort-by :position))]
                        (card-div colours cards packs cycles mwlcards c))
                    ])]])))
        [:div.row-fluid (packlist @cardlist @cards @cycles @packs)]]]])
    

(def tlapp (r/atom {:mwl 15 :pos 20}))

(defn- dedupecards [ cards ]
  (filter (fn [c] (= (:code c) (->> cards (filter #(= (:title c) (:title %))) (map :code) (apply max-key int)))) cards))

(defn- getmwlcards [mwls id cards]
  (let [mwl (->> mwls (filter #(= (:id %) id)) first)
        mwlset (->> mwl :cards keys (map name) set)]
    (->> cards
         (filter #(contains? mwlset (:code %)))
         (dedupecards)
         (sort-by :code)
         (map #(let [k (-> % :code keyword)]
                  (if (-> mwl :cards k :deck_limit zero?)
                      (assoc % :banned true)
                      (if (-> mwl :cards k :is_restricted (= 1))
                          (assoc % :unicorn true)
                          (if-let [ufc (-> mwl k :universal_faction_cost some?)]
                              (assoc % :ufc ufc)
                              (if-let [gp (-> mwl k :global_penalty some?)]
                                (assoc % :global_penalty gp)
                                (assoc % :k k))))))))))
                                
(defn parsedate [ d ]
  (.parse js/Date d))    
  
(defn- cycleclass [ mwl cyc ]
  (let [rot (get (->> rotations (filter #(<= (:cycle %) (:pos @tlapp))) last) :cycles #{})]
    (if (contains? rot (:position cyc))
        "border-danger text-muted bg-light"
        (if (<= (:position cyc) (:pos @tlapp))
            "border-success"
            "text-muted bg-light"))))
    
(defn- cyclerow [cycles mwl flt]
  [:div.d-flex
    (doall (for [cyc (->> cycles (filter flt) (sort-by :position))]
      [:div.p-3.m-2.border.rounded.cycle {
        :style {:cursor "pointer"}
        :key (gensym)
        :class (cycleclass mwl cyc)} 
        (:name cyc)
        ]))])
      
(defn card-link [ c ]
  [:div.mb-1 {:key (:code c)}
    [:a {
      :href (str "https://netrunnerdb.com/en/card/" (:code c)) 
      :target "_blank"
      :data-code (:code c)} (:title c)]])
      
(defn- cycleswithdates [ packs cycles ]
  (map 
    (fn [c]
      (assoc c :date_release (->> packs (filter #(= (:code c) (:cycle_code %))) (sort-by :position) (map :date_release) first)))
    cycles))
  
(defn- timeline [mwls cycs packs cards]
  (let [cycles (->> cycs (filter #(empty? (re-find #"(?i)draft|reprint|napd\smultiplayer" (:name %)))) (cycleswithdates packs))
        mwlcards (getmwlcards mwls (:mwl @tlapp) cards)
        mwl (->> mwls (filter #(= (:id %) (:mwl @tlapp))) first)]
    [:div.row
      [:div.col
        [:div.my-3
          [:input.anrslider {
            :type "range" 
            :min 1 
            :max (->> cycles (map :position) (apply max)) 
            :value (-> @tlapp :pos)
            :on-change #(swap! tlapp assoc :pos (-> % .-target .-value int))}]
          [:div [:span (str "#" (:pos @tlapp) ": up to ")][:span.h5 (->> cycles (take-while #(<= (:position %) (:pos @tlapp))) last :name)]]]
        (cyclerow cycles mwl #(some? (re-find #"(?i)core" (:name %))))
        (cyclerow cycles mwl #(and (= 1 (:size %)) (nil? (re-find #"(?i)core" (:name %)))))
        (cyclerow cycles mwl #(> (:size %) 1))
        
        [:div.row.my-3
          [:div.col
            [:div.btn-group.btn-group-sm
              (doall (for [mwl mwls]
                [:button.btn.btn-outline-secondary {
                  :key (gensym) 
                  :class (if (= (:id mwl) (:mwl @tlapp)) "active")
                  :on-click #(swap! tlapp assoc :mwl (:id mwl))} 
                  [:div (:name mwl)]
                  [:small.muted (:date_start mwl)]]))]]]
        [:div.row
          [:div.col-sm-6
            [:h5 "Runner Cards"]
            [:div.row
              [:div.col-sm-6
                [:div.mb-1 [:b "Restricted"] [:span.ms-2 "🦄"]]
                (for [c (->> mwlcards (filter #(= (:side_code %) "runner")) (filter #(true? (:unicorn %))) (sort-by :title))]
                  (card-link c))]
              [:div.col-sm-6
                [:div.mb-1 [:b "Banned"] [:i.fas.fa-times-circle.text-danger.ms-2]]
                (for [c (->> mwlcards (filter #(= (:side_code %) "runner")) (filter #(true? (:banned %))) (sort-by :title))]
                  (card-link c))]]]
          [:div.col-sm-6
            [:h5 "Corp Cards"]
            [:div.row
              [:div.col-sm-6
                [:div.mb-1 [:b "Restricted"] [:span.ms-2 "🦄"]]
                (for [c (->> mwlcards (filter #(= (:side_code %) "corp")) (filter #(true? (:unicorn %))) (sort-by :title))]
                  (card-link c))]
              [:div.col-sm-6
                [:div.mb-1 [:b "Banned"] [:i.fas.fa-times-circle.text-danger.ms-2]]
                (for [c (->> mwlcards (filter #(= (:side_code %) "corp")) (filter #(true? (:banned %))) (sort-by :title))]
                  (card-link c))]]]]]]))
      
(defn App [] 
  (let [mwl (r/atom "Standard MWL 3.3")
       selected_name (r/atom "Core Set")
       selected_rotated? (r/atom true)]
    (init!)
    (fn []
      [:div.container-fluid.my-3
        [:div.row
          [:div.col
          ; Tablist
            [:ul.nav.nav-tabs.w-100.nav-fill {:role "tablist"}
              [:li.nav-item
                [:a.nav-link.active {:data-bs-toggle "tab" :href "#main" :role "tab"} "Main"]]
              [:li.nav-item
                [:a.nav-link {:data-bs-toggle "tab" :href "#timeline" :role "tab"} "MWL / Rotation Timeline"]]]
          ; Tab Content 
            [:div.tab-content.w-100.py-2
              [:div#main.tab-pane.fade.show.active {:role "tabpanel"}
                (main mwl selected_name selected_rotated?)]
              [:div#timeline.tab-pane.fade {:role "tabpanel"}
                (timeline @mwls @cycles @packs @cards)
                ]]]]])))
                          
(r/render [App] (.getElementById js/document "app"))