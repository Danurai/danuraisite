(ns danuraisite.testapp
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
(def data (r/atom nil))
(def pwned (r/atom #{"core","wla"}))

(defn initdata! []
  (reset! pwned (cljs.reader/read-string (.getItem (.-localStorage js/window) "nrpacks_owned")))
  (go 
    (swap! data assoc :cycles (->> (http/get "/netrunner/api/cycles")
                                   <!
                                   :body 
                                   :data))
    (swap! data assoc :packs  (->> (http/get "/netrunner/api/packs")
                                   <!
                                   :body 
                                   :data))))
    
(defn add-owned-packs! [ packs pwned ]
  (reset! pwned (clojure.set/union @pwned (->> packs (map :code) set)))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))

(defn rmv-owned-packs! [ packs pwned ]
  (doseq [p (map :code packs)]
    (swap! pwned disj p))
  (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))
                                   
(defn- pack-list-group-item [ cycle pack pwned ]
  ^{:key (gensym)}[:div 
    [:input.mr-2 {
      :type "checkbox" 
      :checked (contains? @pwned (:code pack))
      :on-change (fn []
        (if (contains? @pwned (:code pack))
          (swap! pwned disj (:code pack))
          (swap! pwned conj (:code pack)))
        (.setItem (.-localStorage js/window) "nrpacks_owned" @pwned))}]
    [:span (:name pack)]])
                           
(defn- cycle-list-group-item [ cycle packs pwned ]
  (let [rot-icon (if (:rotated cycle)
                    [:i.fas.fa-sync-alt.text-secondary.fa-xs.ml-2 {
                      :title "Rotated"}])
       cyclepacks (->> packs (filter #(= (:cycle_code %) (:code cycle))))
       allpwned?  (clojure.set/subset? (->> cyclepacks (map :code) set) @pwned)]
    ^{:key (gensym)}[:div.list-group-item
      [:div.mb-1        
        [:input.mr-2 {
          :type "checkbox" 
          :checked allpwned?
          :on-change (fn []
            (if allpwned? 
                (rmv-owned-packs! cyclepacks pwned)
                (add-owned-packs! cyclepacks pwned)))}]
        [:span.icon.mr-2 {:class (str "icon-" (:code cycle))}]
        [:span.h5 (:name cycle)]
        rot-icon]
        (if (< 1 (count cyclepacks))
          (doall (for [pack  (sort-by :position cyclepacks)]
            (pack-list-group-item cycle pack pwned))))]))
        
(defn App []
  (initdata!)
  (fn []
    [:div.container.my-3
      [:div.row
        [:div.col-4
          [:div.row-fluid
            [:h4 "Collection"]]
          [:div.row-fluid
            [:div.list-group.w-100
              (let [cycles (:cycles @data)
                    packs (:packs @data)]
                (doall (for [cycle (sort-by :position cycles)]
                  (cycle-list-group-item cycle packs pwned))))]]]]]))
                                   
(r/render [App] (.getElementById js/document "app"))