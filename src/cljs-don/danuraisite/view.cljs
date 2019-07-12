(ns danuraisite.view
  (:require 
    [reagent.core :as r]
    [danuraisite.model :as model]))
    
(def modal-data (r/atom nil))

(defn- get-spec-score [score specs]
  (let [pool (->> specs count (* 2) (- 10))]
    (+ 2 (max score (- pool score)))))

(defn- don-modal []
  (let [spec-score (get-spec-score (:score @modal-data) (:specialisations @modal-data))]
    [:div#don-modal.modal {:role "dialog"}
      [:div.modal-dialog {:role "document"}
        [:div.modal-content
          [:div.modal-header
            [:h5.modal-title (str (:name @modal-data) "/" (:complement @modal-data) " Specialisation")]
            [:button.close {:type "button" :data-dismiss "modal" :aria-label "close"} 
              [:span {:aria-hidden "true"} "x"]]]
          [:div.modal-body
            [:div.input-group
              [:input.form-control {:type "text" 
                                 :value (:specialisation @modal-data) 
                                 :placeholder (-> @modal-data :examples first)
                                 :on-change #(swap! modal-data assoc :specialisation (.. % -target -value))}]
              [:div.input-group-append
                [:div.input-group-text spec-score]]]
            [:div.small.mt-2 (str "Examples: " (:identify @model/hints) )]]
          [:div.modal-footer 
            [:button.btn.btn-secondary {:type "button" 
                                     :data-dismiss "modal"} 
                                    "Close"]
            [:button.btn.btn-primary   {:type "button" 
                                     :data-dismiss "modal"
                                     :disabled (empty? (:specialisation @modal-data))
                                     :on-click (fn [] (model/add-specialisation! @modal-data spec-score)
                                                    (reset! modal-data nil))} 
                                    "Save"]]]]]))
    
(defn- don-slider [statkey]
  (let [stat (-> @model/don-data :stats statkey)
       pool  (- 10 (* 2 (-> stat :specialisations count)))
       specialised? (-> @model/don-data :stats statkey :specialisations empty? not)]
    [:div.row-fluid.border-bottom.mb-2.pb-3
      [:div.d-flex.justify-content-between
          [:div [:b.py-1.px-2.mr-1 (:score stat)][:span (:name stat)]]
          [:a.small ;.btn.btn-sm.btn-outline-primary {
             {:href   "#"
             :data-toggle "modal" 
             :data-target "#don-modal" 
             :on-click #(reset! modal-data (assoc stat :key statkey))}
              "Specialise"]
          [:div [:span (:complement stat)][:b.py-1.px-2.ml-1 (- pool (:score stat))]]]
      [:div
        [:input.custom-range {:type "range"
                           :min 0 
                           :max pool 
                           :value (- pool (:score stat))
                           ;; :disabled specialised?
                           ;; :title (if specialised? "Locked - please remove specialisations to change.")
                           :on-change (fn [e] 
                                        (swap! model/don-data assoc-in [:stats statkey :score ] (- pool (-> e .-target .-value int))))}]]
      [:div
        ; [:div "Specialisations:"]
        [:div
          (for [spec (->> stat :specialisations (sort-by :score) reverse)]
            ^{:key (:score spec)}[:div.border-bottom
                [:button.close {:type "button" :title "Delete specialisation" :on-click #(model/delete-specialisation! statkey (:id spec))} "x"]
                (str (:name spec) ": " (:score spec))])]]]))

(defn- don-input [ key label & params ] 
  [:div.form-group
    [:label.form-label.mr-1.show-tooltip {:title (key @model/hints)} label]
    [:input.form-control (merge (first params) 
                              {:type "text" 
                               :value (key @model/don-data) 
                               :on-change #(swap! model/don-data assoc key (.. % -target -value))})]])
                
(defn- don-sheet
"Dead of Night Character Builder"
  []
  [:div.container.my-2
    ;[:div.row (str @model/don-data)]
    ;[:div.row (str @model/don-victims)]
    [:div.row.p-3
      [:div.col-sm-6
        (don-input :name "Victim Name")
        (don-input :concept "Concept" {:placeholder "Victim is a..."})
        (don-input :badhabits "Bad Habit(s)")
        [:div.form-group
          [:label.form-label.mr-1 "Notes"]
          [:textarea.form-control {:rows "4"
                                :value (:notes @model/don-data)
                                :on-change #(swap! model/don-data assoc :notes (.. % -target -value))}]]
        [:div.form-group
          [:label.form-label "Survival Points"]
          [:div.d-flex.justify-content-center
            (for [n (range 5)]
              ^{:key n}[:i.fas.fa-skull.fa-2x.mr-1])]]]
      [:div.col-sm-6
        [:div.text-center [:b "Attributes"]]
        (don-slider :identify)
        (don-slider :persuade)
        (don-slider :pursue)
        (don-slider :assault)]]
    (don-modal)])
    
(defn don-victim-roll []
  [:div
    [:div.row (str (count @model/don-victims) " Victims...")]
    [:div.row
      [:div.list-group.w-100 
        (for [v @model/don-victims]
          ^{:key (gensym)}[:a.list-group-item.list-group-item-action.flex-column.align-items-start {
            :href "#"
            :on-click (fn [e]
              (.preventDefault e)
              (model/choose-victim (:uid v)))}
            [:div.d-flex.h4 (:name v)
              [:button.btn.close.ml-auto.show-tooltip {:on-click #(false) :title "Remove Victim"} "x"]]
            [:div (:concept v)]
            ])]]])
    
(defn don
"Dead of Night Character Builder"
  []
  [:div.container.my-2
    [:div.row
      [:div.col-sm-3
        [:div.row.h4 "Previous Victims"]
        (if (false? @model/don-victims)
            [:div.row "Log in to see your Victim Roll."]
            [don-victim-roll])]
      [:div.col-sm-9
        [:div.row 
          [:button.btn.btn-primary {:on-click #(model/reset-don!)} "reset"]
          (if (false? @model/don-victims)
            [:div.ml-auto "You must be logged in to Save Victims"]
            [:button.btn.btn-warning.ml-auto {:on-click #(model/savevictim)} "Save"])
          [don-sheet]]]]])