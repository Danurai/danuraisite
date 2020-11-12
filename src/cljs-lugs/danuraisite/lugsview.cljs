(ns danuraisite.lugsview
  (:require 
    [reagent.core :as r]
    [danuraisite.lugsmodel :refer 
      [party apidata savedparties names limits fa-icons 
       markdown party-with-cards 
       selectcard! 
       resetpage! setparty! saveparty! deleteparty! add-hero! delete-hero!]]))

(defn- card-header 
  ([ crd ]
    [:span
      (if-let [specialty (:specialty crd)]
        [:span.mr-2 {:title specialty :class (get @fa-icons specialty "fa fa-question text-warning")}])
      [:span.mr-2 (:name crd)]])
  ([crd level]
    (card-header (level crd))))
    
(defn- card-id [ crd ]
  [:span.text-muted {:style {:position "absolute" :top "0px" :right "5px" :font-size "0.5rem"}} (:id crd)])
    
(defn- select-button [ ps crd ]
  (if (some? (:hero @ps)) 
    [:button.btn.btn-primary.btn-sm {
      :class (if (:hero crd) "disabled")
      :on-click #(selectcard! ps crd)}
      (get-in @party [:heros (:hero crd) :name] "Select")]))
      
;; KIT
(defn- item-row [ ps crd ]
  ^{:key (gensym)}[:li.list-group-item
      [:div.row
        [:div.mb-2 (card-header crd)]
        [:div (select-button ps crd)]
        [card-id crd]]
      [:div.card-deck
        (doall (for [lvl [:basic :upgraded] :let [iteml (lvl crd)]]
          ^{:key (gensym)}[:div.card
            [:div.card-header.p-2 {:data-toggle "collapse" :data-target (str ".collapse-" (:id crd))}
              [:div.mb-1 
                [:b (:name iteml)]]]
            [:div.card-body.collapse.wp-2 {:class (str "collapse-" (:id crd))}
              [:div {:style {:white-space "pre-wrap"}} (markdown (:text iteml) @fa-icons)]]]))]])
          
;; TALENTS

(defn- ability-header [ ab ]
  [:div.d-flex
    [:span.mr-1 (:name ab)]
    [:span.my-auto.ml-auto
      (doall (for [ic (:icons ab)] 
        ^{:key (gensym)}[:span.mr-1 {:title ic :class (get @fa-icons ic)}]))
      (doall (for [co (:cost ab)]
          ^{:key (gensym)}[:span.mr-1 {:title co :class (get @fa-icons co)}]))]])

(defn- talent-abilities 
  ([ crd hero? ]
    [:div.card-group
      (doall (for [ab (:abilities crd)]
        ^{:key (gensym)}[:div.card 
          [:div.card-header.p-2 {:style {:white-space "nowrap" :cursor "pointer"} :data-toggle "collapse" :data-target (str ".collapse-" (:id crd) (if hero? "-h"))} (ability-header ab)]
          [:div.card-body.collapse.p-2 {:class (str "collapse-" (:id crd) (if hero? "-h"))} 
            [:div (doall (for [co (:cost ab)] ^{:key (gensym)}[:span.mr-1 {:title co :class (get @fa-icons co)}]))]
            [:div (-> ab :text (markdown @fa-icons))]]]))])
  ([ crd ]
    (talent-abilities crd false)))
  
(defn- talent-row [ ps crd ]
  ^{:key (gensym)}[:li.list-group-item
      [:div.row
        [:div.mb-2 (card-header crd)]
        [:div (select-button ps crd)]
        [card-id crd]]
      (talent-abilities crd)])

;;;;;;;;;;;      
; WEAPON  ;
;;;;;;;;;;;

(defn- weapon-header [ crd level ]
  [:span.text-muted.mb-1.mr-2
    (if (:twohanded crd) [:span.mr-2 "2H"])
    (if (:melee crd) [:small.mr-1 {:class (re-find #".+(?=\stext-danger)|.+" (get @fa-icons "Melee"))}])
    (if (:ammo (level crd)) [:small.mr-1 {:class (re-find #".+(?=\stext-danger)|.+" (get @fa-icons "Ranged"))}])
    [:span.ml-1 (doall (for [a (:ammo (level crd))] 
      ^{:key (gensym)}[:i {:class (get @fa-icons a)}]))]])

(defn- attack-row [ atk ]
  [:span
    (doall (for [[k v] (sort-by val > atk)]
      ^{:key (gensym)}[:span.rounded.border.border-secondary.mx-1.px-1 {:title (name k)}
        [:span.mr-1 v]
        [:i {:class (get @fa-icons (name k))}]]))])
      
(defn weapon-row [ ps crd level ]
  (let [bcrd (assoc (:basic crd) :specialty (:specialty crd))
        ucrd (assoc (:upgraded crd) :specialty (:specialty crd))]
;; If the active hero owns the weapon, show advanced for selection
;; Option to show advanced stats
    ^{:key (gensym)}[:li.list-group-item
      [:div
        [:div.d-flex.mb-2 
          (card-header bcrd)
          (weapon-header crd :basic)
          [:div (select-button ps crd)]]
        (attack-row (:attack bcrd))
        [card-id crd]]]))
 
;;;;;;;;;;;;;;; 
; OCCUPATION  ;
;;;;;;;;;;;;;;;

(defn- knife []
  (let [crd (->> @apidata :cards (filter #(= (:id %) "GS-WE00")) first)]
    [:span.text-muted.showpopover
      [:i.ml-auto {:class (get @fa-icons "Knife")}]]))

  
(defn- attribute-row [ attributes ]
  [:span.rounded-lg.border.border-secondary.h-100.mx-auto
    (for [att attributes]
      ^{:key (gensym)}[:span {:title (-> att key name)}
        [:span.bg-secondary.text-light.px-2.border.border-secondary (-> att key name (subs 0 2) )][:span.px-2.text-center (-> att val)]]
      )])
        
(defn occupation-row [ ps crd ]
  ^{:key (gensym)}[:li.list-group-item 
    [:div.row
      [:div.col-sm-3
        [:div.mb-2 (card-header crd)]
        [:div (select-button ps crd)]]
      [:div.col-sm-9
        [:div.d-flex.justify-content-around
          (attribute-row (:attributes crd)) (knife)]]
      [card-id crd]]])
        
;; Cards

(defn- card-row [ ps crd ]
  (case (:type crd)
    "Occupation" (occupation-row ps crd)
    "Weapon" (weapon-row ps crd :basic)
    ("Skill Talent" "Weapon Talent") (talent-row ps crd)
    ("Outfit" "Kit") (item-row ps crd)
    ^{:key (gensym)}[:li.list-group-item (:name crd)])
)

;; Heroes 

(defn- simple-card-row [ crd level ]
  (case (:type crd)
    "Occupation" 
      ^{:key (gensym)}[:div.list-group-item.p-2
        [:div.mb-1 (card-header crd) (card-id crd)]
        [:div.d-flex (attribute-row (:attributes crd)) (knife)]]
      "Weapon" 
        ^{:key (gensym)}[:div.list-group-item.p-2
          [:div.mb-1 (card-header crd level) (weapon-header crd level) (card-id crd)]
          [:div.d-flex.justify-content-center (attack-row (-> crd level :attack))]]
      ("Weapon Talent" "Skill Talent")
        ^{:key (gensym)}[:div.list-group-item.p-2
          [:div.mb-1 (card-header crd) (card-id crd)]
          [:small (talent-abilities crd "hero")]]        
      nil))
  
(defn- hero-row [ ps hero ]
  (let [[id h] hero
        herocards (filter #(= (:hero %) id) (:cards @apidata))
        oc (->> herocards (filter #(= (:type %) "Occupation")) first)
        we (->> herocards (filter #(= (:type %) "Occupation")) first)
        ]
  ^{:key id}[:li.list-group-item.mb-2.py-2 {:class (if (= (:hero @ps) id) "border-warning") :on-click #(swap! ps assoc :hero id)}
    [:button.btn.btn-sm.btn-light {
      :style {:position "absolute" :right "1px" :top "1px"}
      :data-toggle "modal" :data-target "#confirm-modal"
      :on-click #(delete-hero! % ps id h) :title "Delete"} "x"]
    [:div.d-flex.mb-2
      [:div.input-group.mr-1
        [:input.form-control {
          :type "text" :value (:name h) 
          :placeholder "Name"
          :on-input #(swap! party assoc-in [:heros (:hero @ps) :name] (-> % .-target .-value))
          :on-change #(swap! party assoc-in [:heros (:hero @ps) :name] (-> % .-target .-value))}]
        (if (= 1 (-> @party :heros count))
          [:div.input-group-append
            [:div.input-group-text [:i.ra.ra-lg.ra-wolf-howl.my-auto {:title "Lone Wolf"}]]])]
      [:select.form-control.mr-2 {:style {:width "auto"} :value (-> h :lvl) :on-change #(swap! party assoc-in [:heros id :lvl] (-> % .-target .-value))}
        (for [n (range 1 10)] ^{:key (gensym)}[:option {:value n} n])]]
    [:ul.list-group
      (doall (for [crd herocards]
        (simple-card-row crd :basic)))]]))
    
    
(defn- editpane [ ps ]
  [:div
    [:div.d-flex.mb-2
      [:h5.mt-auto "Heroes"]
      [:button.btn.btn-sm.btn-light.ml-auto {
        :class (if (> (-> @party :heros count) 3) "disabled") 
        :on-click #(if (< (-> @party :heros count) 4) (add-hero!))}
        "Add Hero to Party"]]
    [:ul.list-group.mb-2
      (doall (for [hero (:heros @party)]
        (hero-row ps hero)))]
    [:form 
      {:on-submit 
        (fn [e] 
          (.preventDefault e) 
          (.stopPropagation e) 
          (when (true? (-> e .-target .checkValidity)) 
            (saveparty! party)
            (swap! ps assoc :lastsave (party-with-cards)))
          (-> e .-target .-classList (.add "was-validated")))}
      [:div.d-flex
        [:input.form-control.mr-2 {
          :type "text" :name "name" :hidden false :placeholder "Party Name" :required true 
          :value (:name @party) :on-change #(swap! party assoc :name (-> % .-target .-value))}]
        (if (= (party-with-cards) (:lastsave @ps))
          [:button.btn.btn-light.mr-2 {
            :on-click (fn [e]
              (.preventDefault e)
              (resetpage! ps))} "Close"]
          [:button.btn.btn-light.mr-2 {
            :data-target "#confirm-modal" :data-toggle "modal" 
            :on-click (fn [e] 
              (.preventDefault e) 
              (swap! ps assoc :confirm {
                :msg (str "Are you sure you want to discard changes to " (:name @party))
                :fn #(resetpage! ps)}))} "Close"])
        [:button.btn.btn-warning {
          :class (if (= (party-with-cards) (:lastsave @ps)) "disabled")
          :role "submit"} "Save"]]]])
    
(defn- loadpane [ ps ]
  @savedparties
  [:div 
    [:div.d-flex.mb-2
      [:h5 "Select Party"]
      [:button.btn.btn-sm.btn-outline-dark.ml-auto {
        :on-click #(setparty! ps nil)}
        "New Party" ]]
    [:ul.list-group
      (doall (for [p @savedparties]
        ^{:key (gensym)}[:li.list-group-item
          [:div.d-flex
            [:strong (:name p)]
            [:button.btn.btn-sm.btn-outline-secondary.ml-auto {
              :role "button"
              :on-click #(setparty! ps p)}
              "Edit"]
            [:button.btn.btn-sm.btn-danger.ml-2 {
              :role "button"
              :data-target "#confirm-modal"
              :data-toggle "modal"
              :on-click (fn []
                (swap! ps assoc :confirm {
                  :msg (str "Are you sure you want to delete " (:name p))
                  :fn (fn []
                    (deleteparty! p)
                    (resetpage! ps))}))
              }
              [:i.fas.fa-trash]]]]))]])

(defn- confirm-modal [ ps ]
  [:div#confirm-modal.modal.fade {:tab-index "-1" :role "modal"}
    [:div.modal-dialog {:role "document"}
      [:div.modal-content
        [:div.modal-header 
          [:h5 "Confirm Action"]
          [:button.close {:type "button" :data-dismiss "modal" :on-click #(swap! ps dissoc :confirm)} "x"]]
        [:div.modal-body (-> @ps :confirm :msg)]
        [:div.modal-footer 
          [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"} "Close"]
          [:button.btn.btn-danger {:type "button" :on-click (-> @ps :confirm :fn) :data-dismiss "modal"} "Confirm"]]]]])
          

(defn Page []
  (let [ps (r/atom {:cardlist "Occupation"})]
    ;(setparty! ps nil)
    ;(swap! ps assoc :hero (-> @party :heros first key))
    (fn []
      [:div.container-fluid.my-3
        [confirm-modal ps]
        [:div.row
          [:div.col-sm-5
            (if (false? @savedparties)
              [:div "You must " [:a {:href "/lu/party/login"} "Log in"] " to create and save parties"]
              (case (:screen @ps)
                :edit [editpane ps]
                [loadpane ps]))
            ;[:div (str "ps: " @ps)]
            ;[:div (str "sp: " @savedparties)] 
            ;[:div (str "pwc: " (party-with-cards))]
            ;[:div (str "cards: " (:cards @apidata))]
          ]
          [:div.col-sm-7
            [:div.sticky-top.pt-2
              [:div.mb-2
                [:div.btn-group.btn-group-sm.d-flex
                  (doall (for [ct (->> @apidata :cards (map :type) distinct) :let [selected? (= (:cardlist @ps) ct)]]
                    ^{:key (gensym)}[:button.btn.btn-outline-dark.w-100 {
                        :class (if selected? "active")
                        :on-click #(swap! ps assoc :cardlist ct)} 
                      ct]))]]
              [:div
                [:ul.list-group
                  (doall (for [crd (->> @apidata :cards (filter #(= (:type %) (:cardlist @ps))) (filter #(not= (:name %) "Knife")) (sort-by :id))]
                    (card-row ps crd)))]]]]]])))