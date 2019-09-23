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

(def cycles   (r/atom nil))
(def packs    (r/atom nil))
(def cardlist (r/atom nil))
(def factions (r/atom nil))
(def types    (r/atom nil))
(def colours  (r/atom nil))
(def pwned   (r/atom nil))

                      
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
      
(defn- buildpages [ packs faction types cards ]
  (let [factioncards (->> cards 
                          (filter #(contains? packs (:pack_code %)))
                          (filter #(= (:faction_code %) faction)))]
    (if (< (count factioncards) 10)
        (concat factioncards (repeat (- 9 (count factioncards)) {:image_url ""}))
        (apply concat
          (for [t (->> types (sort-by :position) (map :code))]
            (let [sector (->> factioncards
                              (filter #(= (:type_code %) t)))]
              (if (< 0 (count sector))
                (concat sector (repeat (- 9 (mod (count sector) 9)) {:image_url ""})))))))))
(defn- initdata! []
  (reset! pwned (cljs.reader/read-string (.getItem (.-localStorage js/window) "nrpacks_owned")))
  (go
    (reset! cycles   (-> (<! (http/get "/netrunner/api/cycles"))  :body :data))
    (reset! packs    (-> (<! (http/get "/netrunner/api/packs"))   :body :data))
    (reset! cardlist (-> (<! (http/get "/netrunner/api/cards"))   :body :data))
    (reset! factions (-> (<! (http/get "/netrunner/api/factions")) :body :data))
    (reset! types    (-> (<! (http/get "/netrunner/api/types"))   :body :data))
    (reset! colours 
      (apply merge 
        (map 
          #(hash-map (:code %) (str "#" (:color %))) 
          (-> (<! (http/get "/netrunner/api/factions")) :body :data))))))
          
(defn add-owned-packs! [ packs pwned ]
  (reset! pwned (clojure.set/union @pwned (->> packs (map :code) set)))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))

(defn rmv-owned-packs! [ packs pwned ]
  (doseq [p (map :code packs)]
    (swap! pwned disj p))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))
                                   
(defn- pack-list-group-item [ cycle pack pageno pwned ]
  ^{:key (gensym)}[:div 
    [:span.mr-2.float-right {    
      :style {:cursor "pointer"}
      :on-click (fn []
        (reset! pageno 1)
        (if (contains? @pwned (:code pack))
          (swap! pwned disj (:code pack))
          (swap! pwned conj (:code pack)))
        (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))}
      [:i.fas.fa-lg {:class (if (contains? @pwned (:code pack)) "fa-toggle-on" "fa-toggle-off")}]]
    [:span (:name pack)]])
                           
(defn- cycle-list-group-item [ cycle packs pageno pwned ]
  (let [rot-icon (if (:rotated cycle)
                    [:i.fas.fa-sync-alt.text-secondary.fa-xs.ml-2 {
                      :title "Rotated"}])
       cyclepacks (->> packs (filter #(= (:cycle_code %) (:code cycle))))
       allpwned?  (clojure.set/subset? (->> cyclepacks (map :code) set) @pwned)]
    ^{:key (gensym)}[:div.list-group-item
      [:div.mb-1        
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
          (pack-list-group-item cycle pack pageno pwned))))]))
          
(defn App []
  (let [pageno (r/atom 1)
       faction (r/atom "anarch")]
    (initdata!)
    (fn []
      (let [clist (buildpages @pwned @faction @types @cardlist)
            numpages (-> clist count (/ 9) Math/ceil)
            currentpage (take 9 (-> clist (nthnext (-> @pageno dec (* 9)))))
            factions @factions]
        [:div.container-fluid.my-3
          [:div.row
            [:div.col-sm-7.mb-3
              [:div.row-fluid.mb-3
                [:ol.breadcrumb
                  (doall (for [f (sort-by :position factions)]
                    ^{:key (gensym)}[:li.breadcrumb-item {
                      :class (if (= (:code f) @faction) "active")
                      :style {:color (get @colours (:code f)) :cursor "pointer"}
                      :on-click (fn [] (reset! faction (:code f)) (reset! pageno 1))}
                      (:name f)]))]]
              [:div.row-fluid.mb-3.d-flex
                [:span (->> currentpage first :type_code str clojure.string/capitalize)]
                [:div.btn-group.btn-group-sm.mx-auto
                  (doall (for [n (range 1 (-> clist count (/ 9) Math/ceil inc))]
                    ^{:key (gensym)}[:button.btn.btn-outline-secondary {:class (if (= n @pageno) "active") :on-click #(reset! pageno n)}
                      n]))]]
              [:div.row.mb-3 ;.sticky-top.pt-2
                (for [c currentpage]
                  ^{:key (gensym)}[:div.col-4.mb-3
                      [:img.img-fluid {:src (if-let [img (:image_url c)] img (clojure.string/replace imgurltemplate #"\{code\}" (:code c)))}]])]]
            [:div.col-sm-5.mb-3
              [:div.row-fluid
                [:h4 "Collection"]]
              [:div.row-fluid
                [:div.list-group.w-100
                  (let [cycles @cycles
                        packs @packs]
                    (doall (for [cycle (sort-by :position cycles)]
                      (cycle-list-group-item cycle packs pageno pwned))))]]]]]))))

(r/render [App] (.getElementById js/document "app"))