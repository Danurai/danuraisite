(ns danuraisite.lugscore
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [danuraisite.lugsmodel :refer [markdown]]))
    
(def apidata (r/atom {}))
(def party (r/atom {}))
(def ps (r/atom {:cardlist "Occupation"}))

(def ^:const names ["Lagather" "Bjorn" "Helmdale" "Rhianna" "Musgrove" "Toad" "Felps" "Germintrude"])

(defn- init! []
  (go 
    (reset! apidata (:body (<! (http/get "/api/data/lugs"))))))

(defn add-hero! []
  (swap! party assoc (gensym "hero") {:name (rand-nth names)}))
  

(defn- occupation-row [ fa-icons occ ]
  [:div.d-flex.justify-content-center
    [:span.rounded-lg.border.border-secondary
      (for [att (:attributes occ)]
        ^{:key (gensym)}[:span
          [:span.bg-secondary.text-light.px-2.border.border-secondary (-> att key name (subs 0 2) )][:span.px-2.text-center (-> att val)]]
        )]])
          
(defn- talent-row [ fa-icons st ]
  [:div.card-deck
    (for [ab (:abilities st)]
      ^{:key (gensym)}[:div.card 
        [:div.card-header.p-2 {:data-toggle "collapse" :data-target (str ".collapse-" (:id st))}
          [:div.d-flex
            [:span.mr-1 (:name ab)]
            (for [ic (:icons ab)]
              ^{:key (gensym)}[:span.ml-1.my-auto {:title ic :class (get fa-icons ic)}])
            (if-let [cost (:cost ab)]
              [:span.ml-auto {:title cost :class (get fa-icons cost)}])]]
        [:div.card-body.collapse.p-2 {:class (str "collapse-" (:id st))} [:span (-> ab :text (markdown fa-icons))]]])
  ])
  
(defn- attack-row [ fa-icons atk ]
  [:span.mx-auto
    (for [[k v] (sort-by val > atk)]
      ^{:key (gensym)}[:span.rounded.border.border-secondary.mx-1.px-1 {:title (name k)}
        [:span.mr-1 v]
        [:i {:class (get fa-icons (name k))}]])])
  
(defn- weapon-row [ fa-icons wp ]
  [:div.card-deck 
    (for [lvl [:basic :upgraded] :let [wpl (lvl wp)]]
      ^{:key (gensym)}[:div.card
        [:div.card-header.p-2 {:data-toggle "collapse" :data-target (str ".collapse-" (:id wp))}
          [:div.mb-1 
            [:b.mr-2 (:name wpl)] (if-let [st (:sub-type wp)] 
            [:small (str " Weapon-" st)])
            (if-let [ammo (:ammo wpl)]
              [:span.float-right
                (if (= "Inf." ammo)
                  [:small.mx-1 [:i.mr-1 {:class (get fa-icons "Ammo")}][:i {:class (get fa-icons "InfiniteAmmo")}]]
                  [:small.mx-1 (for [n (range ammo)] ^{:key (gensym)}[:i.mr-1 {:class (get fa-icons "Ammo")}])])])
          ]
          [:div.d-flex (attack-row fa-icons (:attack wpl))]]
        [:div.card-body.collapse.wp-2 {:class (str "collapse-" (:id wp))}
          [:div [:i (:text wpl)]]
          [:small [:b.mr-1 (-> wp :broken :title)] (-> wp :broken :text (markdown fa-icons))]]])])
          
(defn- item-row [ fa-icons item ]
  [:div.card-deck
    (for [lvl [:basic :upgraded] :let [iteml (lvl item)]]
      ^{:key (gensym)}[:div.card
        [:div.card-header.p-2 {:data-toggle "collapse" :data-target (str ".collapse-" (:id item))}
          [:div.mb-1 
            [:b (:name iteml)]]]
        [:div.card-body.collapse.wp-2 {:class (str "collapse-" (:id item))}
          [:div {:style {:white-space "pre-wrap"}} (markdown (:text iteml) fa-icons)]]])])
    
(defn Page []
  @apidata
  (let [cards (:cards @apidata)
        fa-icons (->> @apidata :icons (map #(hash-map (:name %) (:fa %))) (apply merge))]
    [:div.container-fluid.my-3
      [:div.row
        [:div.col-sm-5
          [:div.d-flex.mb-2
            [:h5.mt-auto "Heroes"]
            [:button.btn.btn-light.ml-auto {
              :class (if (> (count @party) 3) "disabled") 
              :on-click #(if (< (count @party) 4) (add-hero!))}
              "Add Hero to Party"]]
          [:ul.list-group
            (doall (for [[id h] @party]
              ^{:key id}[:li.list-group-item {:class (if (= (:hero @ps) id) "bg-secondary") :on-click #(swap! ps assoc :hero id)}
                [:div
                  [:input {:type "text" :value (:name h) :on-input #(swap! party assoc-in [(:hero @ps) :name] (-> % .-target .-value))}]
                  [:button.btn.btn-sm.btn-danger.float-right {
                    :title "Delete"
                    :on-click (fn [e] 
                      (.stopPropagation e)
                      (if (= id (:hero @ps))
                        (swap! ps dissoc :hero)) 
                      (swap! party dissoc id))}
                    "x"]
                  ]
                [:div (for [c (filter #(= (:hero %) id) cards)] ^{:key (gensym)}[:div (:name c)])]
              ]))]
          [:div (str @party)]
          [:div (str @ps)]
        ]
        [:div.col-sm-7
          [:div.mb-2
            [:div.btn-group.btn-group-sm.d-flex
              (doall (for [ct (->> cards (map :type) distinct) :let [selected? (= (:cardlist @ps) ct)]]
                ^{:key (gensym)}[:button.btn.btn-outline-dark.w-100 {
                  :class (if selected? "active")
                  :on-click #(swap! ps assoc :cardlist ct)} 
                  ct]))]]
          [:div
            [:ul.list-group
              (doall (for [crd (->> cards (filter #(= (:type %) (:cardlist @ps))) (filter #(not= (:name %) "Knife")) (sort-by :id))]
                ^{:key (gensym)}[:li.list-group-item
                  [:div.mb-1
                    (if-let [specialty (:specialty crd)]
                      [:span.mr-2 {:title specialty :class (get fa-icons specialty "fa fa-question text-warning")}])
                    (:name crd)
                    [:button.btn.btn-sm.btn-light.text-muted.ml-3 {
                      :on-click (fn [] 
                        (swap! apidata
                          assoc :cards 
                            (map 
                              #(if (= (:id %) (:id crd))
                                   (if (= (:hero %) (:hero @ps))
                                       (dissoc % :hero)
                                       (assoc % :hero (:hero @ps)))
                                   %) (:cards @apidata))))}
                      (if-let [id (:hero crd)]
                          (get-in @party [id :name])
                          "Select")]
                    [:small.text-muted {:style {:position "absolute" :top "5px" :right "5px"}} (:id crd)] ]
                  (case (:type crd)
                    "Occupation" (occupation-row fa-icons crd)
                    ("Skill Talent" "Weapon Talent") (talent-row fa-icons crd)
                    "Weapon" (weapon-row fa-icons crd)
                    ("Outfit" "Kit") (item-row fa-icons crd)
                    nil)]))]]]
      ]]))
    
    
(add-hero!)
(init!)
(r/render [Page] (.getElementById js/document "app"))