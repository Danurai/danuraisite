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

(def cards    (r/atom nil))
(def cardlist (r/atom nil))
(def cycles   (r/atom nil))
(def packs    (r/atom nil))
(def owned    (r/atom nil))
(def factions (r/atom nil))
(def colours   (r/atom nil))
(def pwned   (r/atom nil))

(go
  (reset! cards    (-> (<! (http/get "/netrunner/api/cards"))   :body :data))
  (reset! cycles   (-> (<! (http/get "/netrunner/api/cycles"))  :body :data))
  (reset! packs    (-> (<! (http/get "/netrunner/api/packs"))   :body :data))
  (reset! pwned    (->> (<! (http/get "/netrunner/api/packs")) 
                      :body 
                      :data
                      (map :code)
                      (filter #(= "true" (get-item %)))
                      set))
  (reset! cardlist (-> (<! (http/get "/netrunner/api/cards"))   :body :data))
  (reset! factions (-> (<! (http/get "/netrunner/api/factions")) :body :data))
  (reset! colours 
    (apply merge 
      (map 
        #(hash-map (:code %) (str "#" (:color %))) 
        (-> (<! (http/get "/netrunner/api/factions")) :body :data)))))
                      


(defn- packs-in-cycle [ packs cycle_code ]
  (->> packs
      (filter #(= (:cycle_code %) cycle_code))
      (sort-by :position)))
      
(defn- toggle-pwned [ pwned pcode ]
  (if (contains? @pwned pcode)
    (do
      (remove-item! pcode)
      (swap! pwned disj pcode))
    (do
      (set-item! pcode "true")
      (swap! pwned conj pcode))))
      
(defn- buildpages [ packs faction cards ]
  (->> cards
      (filter #(= (:faction_code %) faction))
      (filter #(contains? packs (:pack_code %)))))
      
      
(defn App []
  (let [pageno (r/atom 1)
       faction (r/atom "anarch")]
    (fn []
      (let [clist (buildpages @pwned @faction @cardlist)]
        [:div.container-fluid.my-3
          [:div.row
            [:div.col-sm-5
              [:div.row-fluid.mb-2 
                [:h4 "Collection"]]
              [:div.row-fluid
                [:div.list-group.w-100
                  (doall (for [c (->> @cycles (sort-by :position))]
                    (let [packs (packs-in-cycle @packs (:code c))
                         rotated_icon (if (:rotated c) [:i.fas.fa-sync-alt.text-secondary.fa-xs.ml-2 {:title "Rotated"}])]
                      ^{:key (gensym)}[:div.list-group-item
                        [:div {
                          :style {:cursor "pointer"}
                          :on-click #(toggle-pwned pwned (-> packs first :code))
                          }
                          [:i.icon.mr-2 {:class (str "icon-" (:code c))}]
                          [:span (:name c)]
                          rotated_icon
                          [:span.float-right.fas.text-success {
                            :class (if (contains? @pwned (-> packs first :code)) "fa-toggle-on" "fa-toggle-off")
                            }]]
                        (if (< 1 (:size c))
                          (doall (for [p packs]
                            ^{:key (gensym)}[:div.pl-2 {
                              :style {:cursor "pointer"} 
                              :on-click #(toggle-pwned pwned (:code p))
                              }
                                [:i.icon.icon-subroutine.mr-2] 
                                (:name p) rotated_icon
                                [:span.float-right.fas.text-success {
                                  :class (if (contains? @pwned (:code p)) "fa-toggle-on" "fa-toggle-off")
                                  }]
                                ])))])))]
              ]]
            [:div.col-sm-7
              [:div.row.mb-3
                [:nav.nav.nav-pills.flex-sm-row.mb-1.mr-1
                  (doall (for [f (sort-by :position @factions)]
                    ^{:key (gensym)}[:span.nav-item.nav-link {
                      :class (if (= (:code f) @faction) "active")
                      :style {:color (get @colours (:code f))}
                      :on-click #(reset! faction (:code f))}
                      (:name f)]))]]
              [:div.row.mb-3.d-flex
                [:div.btn-group.btn-group-sm.mx-auto ;.btn-group-toggle.btn-group-sm {:data-toggle "buttons" :on-change #(prn (-> % .-target }
                  (doall (for [n (range 1 (-> clist count (/ 9) Math/ceil inc))]
                    ^{:key (gensym)}[:button.btn.btn-outline-secondary {:class (if (= n @pageno) "active") :on-click #(reset! pageno n)}
                      n]))]]
              [:div.row.mb-3.sticky-top.pt-2
                (for [c (take 9 (-> clist (nthnext (-> @pageno dec (* 9)))))]
                  ^{:key (gensym)}[:div.col-4.mb-3
                      [:img.img-fluid {:src (if-let [img (:image_url c)] img (clojure.string/replace imgurltemplate #"\{code\}" (:code c)))}]])]
            ]]]))))

(r/render [App] (.getElementById js/document "app"))