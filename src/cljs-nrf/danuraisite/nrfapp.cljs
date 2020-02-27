(ns danuraisite.nrfapp
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
; virtual folders

(def ^:const imgurltemplate "https://netrunnerdb.com/card_image/{code}.png")

(defn- set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn- get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn- remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))
  
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

(def cycles   (r/atom nil))
(def packs    (r/atom nil))
(def cardlist (r/atom nil))
(def factions (r/atom nil))
(def types    (r/atom nil))
(def colours  (r/atom nil))
(def pwned    (r/atom nil))
(def setcounts (r/atom {:core 1 :core2 1}))

                      
(defn- packs-in-cycle [ packs cycle_code ]
  (->> packs
      (filter #(= (:cycle_code %) cycle_code))
      (sort-by :position)))
      
(defn- remove-pwned-pack! [ pcode ]
  (prn pcode)
  (remove-item! pcode)
  (swap! pwned disj pcode))
  
(defn- add-pwned-pack! [ pcode ]
  (set-item! pcode "true")
  (swap! pwned conj pcode))
  
(defn- toggle-pwned! [ pcode ]
  (if (contains? @pwned pcode)
    (remove-pwned-pack! pcode)
    (add-pwned-pack! pcode)))
    
(defn- dedupesc19 [ cards ]
  (let [titlecounts (->> cards (map :title) frequencies)]
    (filter 
      #(or 
        (= (get titlecounts (:title %) 0) 1)
        (not= (:pack_code %) "sc19"))        
      cards)))
      
(defn- setqty [ cards ]
  (map #(assoc % :qty
    (* (get @setcounts (-> % :pack_code keyword) 1) (:quantity %))) cards))
      
(defn- buildpages [ packs faction types cards ]
;TODO duplicates from SC19?
  (let [factioncards (->> cards 
                          (filter #(contains? packs (:pack_code %)))
                          (filter #(= (:faction_code %) faction))
                          (sort-by :slug) ;:title
                          dedupesc19
                          setqty)
        side (-> factioncards first :side_code)
        typecodes (->> types (sort-by :position) (map :code))]
        (apply concat
          (for [t typecodes]
            (let [sector (->> factioncards (filter #(= (:type_code %) t)))]
              (if (and (not= t (last typecodes)) (< (count factioncards) 19))
                sector
                (if (< 0 (mod (count sector) 9))
                  (concat sector (repeat (- 9 (mod (count sector) 9)) {:blank true :side side}))
                  sector)))))))
                
(defn- initdata! []
  (reset! pwned (cljs.reader/read-string (get-item "nrpacks_owned")))
  (reset! setcounts (cljs.reader/read-string (get-item "nrsets_owned")))
  (go
    (reset! cycles   (-> (<! (http/get "/netrunner/api/cycles"))  :body :data))
    (reset! packs    (-> (<! (http/get "/netrunner/api/packs"))   :body :data))
    (reset! factions (-> (<! (http/get "/netrunner/api/factions")) :body :data))
    (reset! types    (-> (<! (http/get "/netrunner/api/types"))   :body :data))
    (reset! colours 
      (apply merge 
        (map 
          #(hash-map (:code %) (str "#" (:color %))) 
          (-> (<! (http/get "/netrunner/api/factions")) :body :data))))
    (reset! cardlist (->> (<! (http/get "/netrunner/api/cards")) :body :data (map #(assoc % :slug (-> % :title normalise)))))))
          
(defn add-owned-packs! [ packs pwned ]
  (reset! pwned (clojure.set/union @pwned (->> packs (map :code) set)))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))

(defn rmv-owned-packs! [ packs pwned ]
  (doseq [p (map :code packs)]
    (swap! pwned disj p))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))
                                   
(defn- pack-list-group-item [ cycle pack pageno rot-icon pwned ]
  ^{:key (gensym)}[:div.mt-1
    [:span.icon.icon-subroutine.mr-1]
    [:span (:name pack)]
    rot-icon
    [:span.mr-2.float-right {    
      :style {:cursor "pointer"}
      :on-click (fn []
        (reset! pageno 1)
        (if (contains? @pwned (:code pack))
          (swap! pwned disj (:code pack))
          (swap! pwned conj (:code pack)))
        (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))}
      [:i.fas.fa-lg {:class (if (contains? @pwned (:code pack)) "fa-toggle-on" "fa-toggle-off")}]]])
                           
(defn- cycle-list-group-item [ cycle packs pageno showrotated? pwned ]
  (let [rot-icon (if (:rotated cycle)
                    [:i.fas.fa-sync-alt.text-danger.ml-2 {
                      :title "Rotated"}])
       cyclepacks (->> packs (filter #(= (:cycle_code %) (:code cycle))))
       allpwned?  (clojure.set/subset? (->> cyclepacks (map :code) set) @pwned)]
    ^{:key (gensym)}[:div.list-group-item {:hidden (if (and (:rotated cycle) (not showrotated?)) "true")}
      [:div      
        [:span.icon.mr-2 {:class (str "icon-" (:code cycle))}]
        [:span.h5 (:name cycle)]
        rot-icon
        [:span.mr-2.float-right {
          :style {:cursor "pointer"}
          :on-click  (fn []
            (reset! pageno 1)
            (if allpwned? 
                (rmv-owned-packs! cyclepacks pwned)
                (add-owned-packs! cyclepacks pwned)))}
          [:i.fas.fa-2x {:class (if allpwned? "fa-toggle-on" "fa-toggle-off")}]]
        [:span.clearfix]]
      (if (< 1 (count cyclepacks))
        (doall (for [pack  (sort-by :position cyclepacks)]
          (pack-list-group-item cycle pack pageno rot-icon pwned))))]))
          
(defn App []
  (let [pageno       (r/atom 1)
        faction      (r/atom "anarch")
        showrotated? (r/atom false)]
    (initdata!)
    (fn []
      (let [clist (buildpages @pwned @faction @types @cardlist)
            numpages (-> clist count (/ 9) Math/ceil)
            pagecount (-> clist count (/ 9) Math/ceil)
            currentpage (take 9 (-> clist (nthnext (-> @pageno dec (* 9)))))
            factions @factions]
        [:div.container-fluid.my-3
          [:div.row
            [:div.col-sm-7.mb-3
              [:div.row-fluid.mb-3
                (doall (for [side ["runner" "corp"]]
                  ^{:key (gensym)}[:ol.breadcrumb
                    (doall (for [f (->> factions (filter #(= (:side_code %) side)) (sort-by :position))]
                    ^{:key (gensym)}[:li.breadcrumb-item {
                      :class (if (= (:code f) @faction) "active")
                      :style {:color (get @colours (:code f)) :cursor "pointer"}
                      :on-click (fn [] (reset! faction (:code f)) (reset! pageno 1))}
                      (:name f)]))]))]
              [:div.row-fluid.mb-3.d-flex.justify-content-between
                [:span (->> currentpage first :type_code str clojure.string/capitalize)]
                [:span (str "Page " @pageno " of " pagecount)]
                [:div.btn-group.btn-group-sm ;{:style {:width "15%"}}
                  [:button.btn.btn-outline-secondary {
                    :type "button" 
                    :on-click #(reset! pageno (max 1 (dec @pageno)))} "<<"]
                  ;[:input.form-control {:type "text" :value @pageno :on-change #(reset! pageno (-> % .-target .-value))}]
                  [:button.btn.btn-outline-secondary {
                    :type "button"
                    :on-click #(reset! pageno (min (inc @pageno) pagecount))} ">>"]]]
              [:div.row.mb-3 ;.sticky-top.pt-2
                (for [c currentpage]
                  ^{:key (gensym)}[:div.col-4.mb-3
                      [:div {:style {:position "relative"}}
                        (if (not (:blank c))
                          [:span.py-1.px-2 {
                            :style {
                              :position "absolute" :left "5px" :bottom "5px"
                              :background-color "white" :opacity "0.8":border-radius "8px"}}
                            (str "x" (:qty c))])
                        [:img.img-fluid {
                          :title (:pack_code c)
                          :style {:opacity (if (:blank c) "0.3" "1")} 
                          :src (if (:blank c)
                                   (str "/img/" (:side c) "_back.png")
                                   (if-let [img (:image_url c)] 
                                     img
                                     (clojure.string/replace imgurltemplate #"\{code\}" (:code c))))}]]])]]
            [:div.col-sm-5.mb-3
              [:div.row-fluid.mb-2
                [:ul.nav.nav-tabs.nav-fill.mb-2
                  [:li.nav-item [:a.nav-link.active {:data-toggle "tab" :href "#collection" :role "tab"} "Collection"]]
                  [:li.nav-item [:a.nav-link {:data-toggle "tab" :href "#counts" :role "tab"} "Set counts"]]]
                [:div.tab-content
                  [:div#collection.tab-pane.fade.active.show.my-2 {:role "tabpanel"}
                    [:button.btn.btn-sm.btn-outline-secondary.float-right.mb-2 {
                      :class (if @showrotated? "active")
                      :on-click #(reset! showrotated? (not @showrotated?))} 
                      (if @showrotated? "Hide Rotated" "Show Rotated")]
                    [:div.list-group.w-100
                      (let [cycles @cycles
                            packs @packs]
                        (doall (for [cycle (sort-by :position cycles)]
                          (cycle-list-group-item cycle packs pageno @showrotated? pwned))))]]
                  [:div#counts.tab-pane.fade.my-2 {:role "tabpanel"}
                    [:div.d-flex.justify-content-between
                      [:span.mr-2.w-25 "Core 1.0"]
                      [:span.text-muted (-> @setcounts :core str)]
                      [:input.custom-range.w-50 {
                        :type "range"
                        :max 3 
                        :min 1
                        :value (:core @setcounts) 
                        :on-change (fn [e]
                          (swap! setcounts assoc :core (-> e .-target .-value))
                          (set-item! "nrsets_owned" @setcounts))}]]
                    [:div.d-flex.justify-content-between
                      [:span.mr-2.w-25 "Core 2.0"]
                      [:span.text-muted (-> @setcounts :core2 str)]
                      [:input.custom-range.w-50 {
                        :type "range"
                        :max 3 
                        :min 1
                        :value (:core2 @setcounts) 
                        :on-change (fn [e]
                          (swap! setcounts assoc :core2 (-> e .-target .-value))
                          (set-item! "nrsets_owned" @setcounts))}]]
                  ]
                ]]]]]))))

(r/render [App] (.getElementById js/document "app"))