(ns danuraisite.mwlapp
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
(enable-console-print!)

(def cycles (r/atom nil))
(def mwls (r/atom nil))
(def packs (r/atom nil))
(def cardlist (r/atom nil))
(def cards (r/atom nil))
(def factions (r/atom nil))
(def colours (r/atom nil))
(def types (r/atom nil))

(go
  (reset! cycles   (-> (<! (http/get "/netrunner/api/cycles"))   :body :data))
  (reset! packs    (-> (<! (http/get "/netrunner/api/packs"))    :body :data))
  (reset! mwls     (-> (<! (http/get "/netrunner/api/mwl"))      :body :data))
  (reset! types    (-> (<! (http/get "/netrunner/api/types"))    :body :data))
  (let [cardsapi (<! (http/get "/netrunner/api/cards"))]
    (reset! cards    (-> cardsapi :body :data))
    (reset! cardlist (->> cardsapi :body :data (filter #(= (:pack_code %) "core")))))
 ; (reset! factions (-> (<! (http/get "/api/factions")) :body :data)))
  (reset! colours 
    (apply merge 
      (map 
        #(hash-map (:code %) (str "#" (:color %))) 
        (-> (<! (http/get "/netrunner/api/factions")) :body :data)))))
          
(defn- packtags [cards packs cycles c]
  (let [otherpacks (->> cards 
                      (filter #(= (:title %) (:title c)))
                      (map :pack_code))
       othercycles (->> packs
                      (filter #(some #{(:code %)} otherpacks))
                      (map :cycle_code))]
    (for [oc (filter #(some #{(:code %)} othercycles) cycles)]
      ^{:key (gensym)}[:i.icon.mr-2 {
        :class (str "icon-" (:code oc) (if (:rotated oc) " text-danger")) 
        :title (str (:name oc) (if (:rotated oc) " !Rotated")) }])))
  
      
(defn- packs-in-cycle [ packs cycle_code ]
  (->> packs
      (filter #(= (:cycle_code %) cycle_code))
      (sort-by :position)))
      
(defn- parsedeck! [ decklist cards clist sname srot? ]
  (let [lines (re-seq #".+" decklist)]
    (reset! sname (first lines))
    (reset! srot? false)
    (reset! clist 
      (apply conj 
        (filter #(= (:title %) (nth lines 1)) cards)
        (mapv (fn [[a b c d]]
          (first (filter #(= (:title %) (if c c d)) cards)))
          (re-seq #"[0-9]x\s((.+?)\s\u25CF|(.+))" decklist))))))
        
(defn- card-div [ colours cards packs cycles mwl c ]
  (let [cardmwl (-> mwl (get (-> c :code str keyword)))]
    ^{:key (gensym)}[:div {:style {:color (get colours (:faction_code c))}}
      [:span
        [:a.cardlink {:data-code (:code c) :data-image_url (:image_url c)}
          (if (:is_restricted cardmwl)
            [:i.fas.fa-exclamation.text-warning.mr-2 {:title "restricted"}])
          (if (= 0 (:deck_limit cardmwl))
            [:i.fas.fa-times-circle.text-danger.mr-2 {:title "banned"}])
          [:span.mr-2 (str (if (:uniqueness c) "\u2022 ") (:title c))]
          (repeat (:universal_faction_cost cardmwl) ^{:key (gensym)}[:i.fas.fa-circle.mr-1.fa-xs])
          (repeat (:global_penalty cardmwl) ^{:key (gensym)}[:i.fas.fa-circle.mr-1.fa-xs])]
        (packtags cards packs cycles c)]]))
        
    
(defn App [] 
  (let [mwl (r/atom "Standard MWL 3.3")
       selected_name (r/atom "Core Set")
       selected_rotated? (r/atom true)]
    (fn []
      [:div.container-fluid.my-3
        [:div.row
          [:div.col-sm-4 
            [:div.row-fluid
              [:ul.nav.nav-tabs.w-100.nav-fill {:role "tablist"}
                [:li.nav-item
                  [:a.nav-link.active {:data-toggle "tab" :href "#packs" :role "tab"} "Packs"]]
                [:li.nav-item
                  [:a.nav-link {:data-toggle "tab" :href "#deck" :role "tab"} "Deck"]]]
              [:div.tab-content.w-100.py-2
                [:div#packs.tab-pane.fade.show.active {:role "tabpanel"}
                  [:div.list-group.w-100
                    (doall (for [c (->> @cycles (sort-by :position))] ; reverse)]
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
                            [:i.icon.mr-2 {:class (str "icon-" (:code c))}]
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
                                  [:i.icon.icon-subroutine.mr-2] 
                                  (:name p)
                                  [:span.float-right rotated_icon]]))])))]]
                [:div#deck.tab-pane.fade {:role "tabpanel"}
                  [:h5.mb-2 "Paste Decklist Below"]
                  [:textarea.form-control {
                    :rows 25
                    :on-input #(parsedeck! (-> % .-target .-value) @cards cardlist selected_name selected_rotated?)}]]]]]
          [:div.col-sm-8
            [:div.sticky-top.pt-2
              [:div.row-fluid.d-flex.mb-2
                [:span.w-50.h4 
                  [:span @selected_name]
                  [:span {:hidden (false? @selected_rotated?)}
                    [:span.fas.fa-sync-alt.text-danger.ml-2 {:title "Rotated"}]]]
                [:select.form-control.w-50.ml-auto {:value @mwl :on-change #(reset! mwl (-> % .-target .-value))}
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
                (for [sc (->> @cardlist (map :side_code) distinct)]
                  ^{:key (gensym)}[:div
                    [:div.row-fluid.font-weight-bold (clojure.string/capitalize sc)]
                    [:div.row-fluid {:style {
                      :-webkit-column-gap "20px" :-webkit-column-count 3
                      :-moz-column-gap "20px" :-moz-column-count 3
                      :column-gap "20px" :column-count 3 }}
                      (for [tc (->> typeset (filter #(= (:side_code %) sc)) (apply merge id) (sort-by :position) (map :code))]
                        ^{:key (gensym)}[:div {:style {:break-inside "avoid"}}
                          [:div [:u (clojure.string/capitalize tc)]]
                            (for [c (->> cardset (filter #(= (:side_code %) sc)) (filter #(= (:type_code %) tc)) (sort-by :position))]
                              (card-div colours cards packs cycles mwlcards c))
                          ])]]))]]]])))

(r/render [App] (.getElementById js/document "app"))