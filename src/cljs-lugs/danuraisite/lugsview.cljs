(ns danuraisite.lugsview
  (:require 
    [reagent.core :as r]
    [danuraisite.lugsmodel :refer 
      [party apidata savedparties names levels sets fa-icons
       markdown party-with-cards get-occupation hero-upgrade-talent-count
       selectcard! 
       resetparty! setparty! saveparty! deleteparty! add-hero! delete-hero! upgrade-toggle! hero-upgrade!]]))

(defn- card-header [ crd ]
  (prn (:speciality crd))
  [:span.mr-2
    [:span.mr-2 (:name crd)]
    (if-let [speciality (:speciality crd)]
      [:i.fa-sm {:title speciality :class (get @fa-icons speciality "fa fa-question text-warning")}])])
    
(defn- card-id [ crd ]
  [:span.text-muted {:style {:position "absolute" :bottom "0px" :right "5px" :font-size "0.5rem"}} (:id crd)])
        
(defn- select-button [ crd ]
  (let [hvec (reduce conj [nil] (-> @party :heros keys sort))
        occupations (apply merge (map #(hash-map % (get-occupation %)) hvec))
        max (-> hvec count)]
    (if-not (nil? @party)
      [:div.btn-group.btn-group-xs.mr-2.mb-1
        (doall (for [n (range 1 max)
                :let [id (-> hvec (.indexOf (:hero crd)))]]
          [:button.btn.btn-outline-dark {
            :key (gensym) 
            :class (if (= n id) "active")
            :on-click #(selectcard! (get hvec n) crd)} 
            (if-let [occ (get occupations (get hvec n))]
              (if (empty? occ)
                  n
                  [:span {:class (get @fa-icons (-> occ first :speciality))}])
              n)]))])))
      
(defn- upgrade-btn [ crd ]
  [:div.mr-2
    [:button.btn.btn-xs{
      :title "Upgrade"
      :class (if (:upgrade? crd) "active btn-success" "btn-outline-primary")
      :on-click #(upgrade-toggle! crd)} 
      [:b (if (:upgrade? crd) "U" "B")]
      ]])
      
          
;; TALENTS

(defn- ability-header [ ab ]
  [:div.d-flex
    [:span
      (doall (for [co (:cost ab)]
        [:span.mr-1 {:key (gensym) :title co :class (get @fa-icons co)}]))]
    [:span.mr-1 (:name ab)]
    [:span.my-auto.ml-auto
      (doall (for [ic (:icons ab)] 
        [:span.mr-1 {:key (gensym) :title ic :class (get @fa-icons ic)}]))
      ]])

(defn- talent-abilities 
  ([ crd hero? ]
    [:div.card-group
      (doall (for [ab (:abilities crd)]
        ^{:key (gensym)}[:div.card 
          [:div.card-header.p-2 {:style {:white-space "nowrap" :cursor "pointer"} :data-toggle "collapse" :data-target (str ".collapse-" (:id crd) (if hero? "-h"))} (ability-header ab)]
          [:div.card-body.collapse.p-2 {:class (str "collapse-" (:id crd) (if hero? "-h"))} 
            ;[:div (doall (for [co (:cost ab)] ^{:key (gensym)}[:span.mr-1 {:title co :class (get @fa-icons co)}]))]
            [:div (-> ab :text (markdown @fa-icons))]]]))])
  ([ crd ]
    (talent-abilities crd false)))
  
(defn- talent-row [ crd ]
  [:div.d-flex.flex-wrap
    (select-button crd)
    (card-header crd)
    [:div.w-100 (talent-abilities crd)]])

;;;;;;;;;;;      
; WEAPON  ;
;;;;;;;;;;;

;(defn- knife []
;  (let [crd (->> @apidata :cards (filter #(= (:id %) "GS-WE00")) first)]
;    [:span.text-muted.showpopover.px-1
;      [:i {:class (get @fa-icons "Knife")}]]))

(defn- weapon-header [ crd ]
  [:span.text-muted.mb-1.mr-2
    (if (:twohanded crd) [:span.mr-2 "2H"])
    (if (:melee crd) [:small.mr-1 {:class (re-find #".+(?=\stext-danger)|.+" (get @fa-icons "Melee"))}])
    (if (:ammo crd) [:small.mr-1 {:class (re-find #".+(?=\stext-danger)|.+" (get @fa-icons "Ranged"))}])
    [:span.ml-1 
      (doall (for [a (:ammo crd)] 
        [:i {:key (gensym) :class (get @fa-icons a)}]))]])

(defn- attack-row [ atk ]
  [:div.mt-auto
    (doall (for [[k v] (sort-by val > atk)]
      ^{:key (gensym)}[:span.rounded.border.border-secondary.mx-1.px-1 {:title (name k)}
        [:span.mr-1 v]
        [:i {:class (get @fa-icons (name k))}]]))])
      
(defn weapon-row [ crd level ]
  (let [lvl (if (:upgrade? crd) :upgraded :basic)
        crd (merge crd (lvl crd))]
    [:div.d-flex {:style {:flex-wrap "wrap"}}
      (select-button crd)
      (card-header crd)
      (weapon-header crd)
      [:div.d-flex.ml-auto (attack-row (:attack crd)) (upgrade-btn crd) ]]))
      
;;;;;;;     
; KIT ;
;;;;;;;

(defn- item-row [ crd ]
  (let [lvl (if (:upgrade? crd) :upgraded :basic)
        crd (merge crd (lvl crd))]
    [:div.d-flex.flex-wrap
      [select-button crd]
      [card-header crd]
      [:div.ml-auto.d-flex
        (if (some? (:attack crd)) (attack-row (:attack crd)))   
        [upgrade-btn crd]]
      [:div.w-100.text-center {:style {:white-space "pre-wrap"}} (-> crd :text (markdown @fa-icons))]]))
 
;;;;;;;;;;;;;;; 
; OCCUPATION  ;
;;;;;;;;;;;;;;;

(defn- attribute-row [ attrs ]
  [:div.mt-auto
    (for [[k v] attrs]
      [:span.rounded.border.border-secondary.mx-1.px-1 {:key (gensym) :title (name k)}
        [:span.mr-1 (-> k name (subs 0 2))]
        [:b v]])])
        
(defn occupation-row [ crd ]
  [:div.d-flex {:style {:flex-wrap "wrap"}}
    (select-button crd)
    (card-header crd)
    [:div.ml-auto (attribute-row (:attributes crd))]])
        
;; Cards

(defn- card-row [ crd ]
  [:li.list-group-item {:key (gensym)}
    [card-id crd]
    (case (:type crd)
      "Occupation" (occupation-row crd)
      "Weapon" (weapon-row crd :basic)
      ("Skill Talent" "Weapon Talent") (talent-row crd)
      ("Outfit" "Kit" "Companion") (item-row crd) 
      [:div.d-flex (:name crd)])])

;; Heroes 
(defn- hero-upgrade-btn [ hero crd ]
  (let [upgrade? (contains? (-> hero val :upgrades) (:id crd))]
    [:div.mr-2
      [:button.btn.btn-xs {
        :class (if upgrade? "btn-success" "btn-outline-primary")
        :on-click #(hero-upgrade! hero crd)}
        [:b (if upgrade? "U" "B")]]]
      ))
(defn- remove-btn [ hero crd ]
  [:div.ml-2 [:button.btn.btn-xs.btn-light {
    :title (str "Remove " (:name crd)) 
    :on-click #(selectcard! hero crd)} 
    [:i.text-secondary.fa-sm.fas.fa-times]
    ]])

(defn- simple-card-row [ hero crd ]
  (let [lvl (if (contains? (-> hero val :upgrades) (:id crd)) :upgraded :basic)
        crd (if (-> crd :basic some?) (merge crd (lvl crd)) crd)] 
  [:li.list-group-item {:key (gensym) :style {:position "relative"}}
    ;(card-id crd)
    (case (:type crd)
      "Occupation" 
        [:div.d-flex.flex-wrap
          [:div (attribute-row (:attributes crd)) ]
          [:div.ml-auto (card-header crd)]]
      "Weapon" 
        [:div.d-flex.flex-wrap
          [:div (card-header crd) (weapon-header crd)]
          [:div.d-flex.ml-auto 
            (hero-upgrade-btn hero crd)
            (attack-row (:attack crd))]
          (remove-btn hero crd)]
      ("Weapon Talent" "Skill Talent")
        [:div.d-flex.flex-wrap
          [:div (card-header crd)]
          [:div.ml-auto (remove-btn hero crd)]
          [:div.w-100 [:small (talent-abilities crd "hero")]]]
      ("Outfit" "Kit" "Companion")
        [:div.d-flex.flex-wrap
          (hero-upgrade-btn hero crd)
          [:div (card-header crd)]
          [:div.ml-auto (remove-btn hero crd)]
          [:small.w-100.text-center {:style {:white-space "pre-wrap"}}(:text crd)]]
      nil)]))
    
(defn- card-comparator [ crd ]
  (str 
    (.indexOf ["Occupation" "Outfit" "Kit" "Companion" "Weapon" "Weapon Talent" "Skill Talent"] (:type crd))
    (:id crd)))
    
(defn count-limits [ id h lonewolf? levels ]
  (let [card-counts (hero-upgrade-talent-count id)
        totals (update card-counts :upgrade + (-> :upgrades h count))
        limits (-> (levels (:lvl h))
                    (update :upgrade + (if lonewolf? 1 0))
                    (update :talent + (if lonewolf? 2 0)))]
    [:div.mb-1.d-flex
      [:span.mr-2.ml-auto
        [:span "Upgrades: "]
        [:b {:class (case (- (:upgrade limits) (:upgrade totals)) 0 "text-success" -1 "text-primary" "text-danger")}
          (:upgrade totals)]
        [:span (str "/" (:upgrade limits))]]
      [:span.mr-2
        [:span "Talents: "]
        [:b {:class (case (- (:talent limits) (:talent totals)) 0 "text-success" -1 "text-primary" "text-danger")}
          (:talent totals)]
        [:span (str "/" (:talent limits))]]]))

(defn- hero-row [ ps hero ]
  (let [[id h] hero
        herocards (filter #(= (:hero %) id) (:cards @apidata))
        lonewolf? (= 1 (-> @party :heros count))]
  [:li.list-group-item.mb-2.py-2.pl-2 {:key id}
    [:button.btn.btn-sm.btn-light {
      :style {:position "absolute" :right "1px" :top "1px"}
      :data-toggle "modal" :data-target "#confirm-modal"
      :on-click #(delete-hero! % ps id h) :title "Delete"} "x"]
    [:div.d-flex.mb-2
      (if-let [occupation (filter #(= "Occupation" (:type %)) herocards)]
        [:div.d-flex
          [:b (card-header (first occupation))]
          (attribute-row (-> occupation first :attributes))
          ])
      [:div.ml-auto [count-limits id h lonewolf? levels]]]
    [:div.d-flex.mb-2
      [:label.my-auto.mr-2 "Name"]
      [:div.input-group.mr-2
        [:input.form-control {
          :type "text" :value (:name h)
          :placeholder "Name"
          :on-change #(swap! party assoc-in [:heros id :name] (-> % .-target .-value))}]
        (if lonewolf?
          [:div.input-group-append
            [:div.input-group-text [:i.ra.ra-lg.ra-wolf-howl.my-auto {:title "Lone Wolf"}]]])]
      [:label.my-auto.mr-2 "Level"]
      [:select.form-control.mr-2 {:style {:width "auto"} :value (-> h :lvl) :on-change #(swap! party assoc-in [:heros id :lvl] (-> % .-target .-value))}
        (for [n (range 1 10)] ^{:key (gensym)}[:option {:value n} n])]]
    [:ul.list-group
      (doall (for [crd (->> herocards (filter #(not= "Occupation" (:type %))) (sort-by card-comparator))]
        (simple-card-row hero crd)))]]))
    
(defn- editpane [ ps ]
  [:div
    [:form.form.mb-2 
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
              (resetparty!))} "Close"]
          [:button.btn.btn-light.mr-2 {
            :data-target "#confirm-modal" :data-toggle "modal" 
            :on-click (fn [e] 
              (.preventDefault e) 
              (swap! ps assoc :confirm {
                :msg (str "Are you sure you want to discard changes to " (:name @party))
                :fn #(resetparty!)}))} "Close"])
        [:button.btn.btn-warning {
          :class (if (= (party-with-cards) (:lastsave @ps)) "disabled")
          :role "submit"} "Save"]]]
    [:div.d-flex.mb-2
      [:h5.my-2 "Heroes"]
      [:button.btn.btn-sm.btn-outline-secondary.ml-auto {
        :class (if (> (-> @party :heros count) 3) "disabled") 
        :on-click #(if (< (-> @party :heros count) 4) (add-hero!))}
        "Add Hero to Party"]]
    [:ul.list-group.mb-2
      (doall (for [hero (:heros @party)]
        (hero-row ps hero)))]])
    
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
                    (resetparty!))}))
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

(defn filter-cards [ flt ]
  (->> @apidata 
      :cards 
      (filter #(= (:type %) (:cardlist flt))) 
      (filter #(not= (:name %) "Knife"))
      (filter #(contains? (:setlist flt) (-> % :id (subs 0 2))))    
      (sort-by :id)))

(defn Page []
  (let [ps (r/atom {:cardlist "Occupation" :setlist (->> sets (map :id) set)})]
    (fn []
      [:div.container-fluid.my-2
        [confirm-modal ps]
        [:div.row
          [:div.col-lg-6
            (if (false? @savedparties)
              [:div "You must " [:a {:href "/lu/party/login"} "Log in"] " to create and save parties"]
              (if (nil? @party)
                [loadpane ps]
                [editpane ps]))
            ;[:div (str "ps: " @ps)]
            ;[:div (str "sp: " @savedparties)] 
            ;[:div (str @party)]
            ;[:div (str "pwc: " (party-with-cards))]
            ;[:div (str "cards: " (:cards @apidata))]
          ]
          [:div.col-lg-6
            [:div.mb-2
              [:div (doall (for [set sets]
                [:div.form-check.form-check-inline {
                  :key (gensym)}
                  [:input.form-check-input {
                    :type "checkbox" 
                    :checked (contains? (:setlist @ps) (:id set)) 
                    :on-change #(if (contains? (:setlist @ps) (:id set)) 
                                   (swap! ps update-in [:setlist] disj (:id set))
                                   (swap! ps update-in [:setlist] conj (:id set)))}]
                  [:label.form-check-label (:name set)]]))]]
            [:div.mb-2
              [:div.btn-group.btn-group-sm.d-flex
                (doall (for [ct (->> @apidata :cards (map :type) distinct) :let [selected? (= (:cardlist @ps) ct)]]
                  ^{:key (gensym)}[:button.btn.btn-outline-dark.w-100 {
                      :class (if selected? "active")
                      :on-click #(swap! ps assoc :cardlist ct)} 
                    ct]))]]
            [:div
              [:ul.list-group
                (doall (for [crd (sort-by card-comparator (filter-cards @ps))]
                  (card-row crd)))]]]]])))