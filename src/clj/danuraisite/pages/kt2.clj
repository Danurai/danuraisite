(defn killteam [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div#app]]
    (h/include-js "/js/compiled/kt-app.js")
    (h/include-css "/css/rpg-awesome.min.css?v=1")
    (h/include-css "/css/kt.css")))

(defn- get-rp [ reqs specops ]
; order by time so RP never goes over 5, or save RP independently 
  (-> 4
      (- (->> reqs (remove #(= (:type %) "Mission Complete")) count))
      (+ (->> reqs (filter #(= (:type %) "Mission Complete")) (map #(let [v (:value %)] (if (empty? v) 1 (read-string v)))) (apply +)))
      (+ (->> specops (filter #(= (:progress %) 6)) (map :rp) (apply +)))
      (min 5)))

(defn- get-ep [ reqs equip ]
  (-> 5
      (* (->> reqs (filter #(= (:type %) "Equipment Drop")) count))
      (- (->> equip (map :ep) (apply +)))
      ))

(defn kt2dataslate [ req ]
  (let [uid (-> req :params :id)
        data (if uid (first (db/getspecop uid)))
        requisitions (if uid (db/getrequisitions uid))
        specops (if uid (db/getspecopsspecops uid))
        equipment (if uid (db/getspecopsequipment uid))
        tab (:query-string req)]  
    (h/html5
      pretty-head
      [:body
        (navbar req)
        [:div.container.my-3
          [:div.row
            [:div.col-sm-3
              [:h5.text-center "Saved Spec Ops"]
              [:div.list-group
                (for [specop (db/getspecops)]
                  [:a.list-group-item {:href (str "/killteam/specops/" (:uid specop))} 
                    [:h5 (:name specop)]
                    [:div (:faction specop "(empty)")]])]]
            [:div.col-sm-9
              [:form {:action "/killteam/specops/save" :method "POST"}
                [:div.d-flex.mb-2
                  [:h5.ms-auto.me-auto "Spec Ops Kill Team"]
                  [:button.btn.btn-primary.ms-auto {:type "submit"} "Save"]]
                [:div.d-flex.mb-2
                  [:b.me-3 "Requisition Points: " (get-rp requisitions specops)]
                  [:b.me-3 "Equipment Points: " (get-ep requisitions equipment)]]
                [:input#uid {:hidden true :name "uid" :value uid}]
                [:div.row.mb-2
                  [:label.col-sm-3.col-form-label "Kill Team Name"]
                  [:div.col-sm-9
                    [:input.form-control {:type "text" :name "name" :required true :value (:name data)}]]]
                [:div.row.mb-2
                  [:label.col-sm-3.col-form-label "Faction/Selectable Keyword"]
                  [:div.col-sm-9
                    [:input.form-control {:type "text" :name "faction" :value (:faction data)} ]
                    [:input.form-control {:type "text" :name "selectable" :value (:selectable data)} ]]]
                [:div.row.mb-2
                  [:label.col-sm-3.col-form-label "Base of Operations"]
                  [:div.col-sm-9
                    [:input.form-control {:type "text" :name "base" :value (:base data)} ]]]
                (for [asset (filter #(= (:type %) "Asset Acquired") requisitions)]
                  [:div.col-sm-9.offset-sm-3.mb-2 [:div.ms-2 (:note asset)]])
                [:div.row.mb-2
                  [:label.col-sm-3.col-form-label "History"]
                  [:div.col-sm-9
                    [:input.form-control {:type "text" :name "history" :value (:history data)} ]]]
                [:div.row.mb-2
                  [:label.col-sm-3.col-form-label "Quirks"]
                  [:div.col-sm-9
                    [:input.form-control {:type "text" :name "quirks" :value (:quirks data)} ]]]
                ;[:div.row.mb-2
                ;  [:label.col-sm-3.col-form-label "Notes"]
                ;  [:div.col-sm-9
                ;    [:input.form-control {:type "text" :name "notes" :value (:notes data)} ]]]
                ]
                (if (some? data)
                  [:div 
                    [:ul.nav.nav-tabs.nav-justified {:role "tablist"}
                      [:li.nav-item {:role "presentation"} [:button {:class (if (nil? tab) "nav-link active" "nav-link") :data-bs-toggle "tab" :data-bs-target "#requisition" :type "button" :role "tab"} "Requisitions"]]
                      [:li.nav-item {:role "presentation"} [:button {:class (if (= tab "specops") "nav-link active" "nav-link") :data-bs-toggle "tab" :data-bs-target "#specops"     :type "button" :role "tab"} "Spec Ops"]]
                      [:li.nav-item {:role "presentation"} [:button {:class (if (= tab "equipment") "nav-link active" "nav-link") :data-bs-toggle "tab" :data-bs-target "#equipment"   :type "button" :role "tab"} "Equipment"]]]
                    [:div.tab-content
                      [:div#requisition {:class (if (nil? tab) "tab-pane fade my-2 show active" "tab-pane fade my-2") :role "tabpanel"}
                        [:form.mb-2 {:action "/killteam/specops/addrequisition" :method "POST"}
                          [:div.d-flex.mb-2
                            [:h5 "Add Requisition"]
                            [:button.btn-sm.btn-warning.ms-auto {:role "submit"} "Add"]]
                          [:input {:hidden true :name "specop" :type "number" :readonly true :value uid}]
                          [:div.row 
                            [:div.col-sm-3 
                              [:select.form-select {:name "type"}
                                [:option {:selected true} "Mission Complete"]
                                [:option "Operative Assigned"]
                                [:option "Equipment Drop"]
                                [:option "Asset Acquired"]
                                [:option "Medivac"]
                                [:option "Recuperate"]
                                [:option "Proficient Operative"]
                                [:option "Weaponsmith"]]]
                            [:div.col-sm-4 [:input.form-control {:type "text" :placeholder "Value" :name "value"}]]
                            [:div.col-sm-5 [:input.form-control {:type "text" :placeholder "Notes" :name "note"}]]]]
                        [:div [:b "Requisition History"]]
                        [:ul.list-group.list-group-flush
                          (for [sor requisitions]
                            [:li.list-group-item 
                              [:div.me-2 (:type sor) " (" (if (= "" (:value sor)) 1 (:value sor)) " RP)"] [:small (:note sor)]])]]
                      [:div#specops {:class (if (= tab "specops") "tab-pane fade my-2 show active" "tab-pane fade my-2") :role "tabpanel"}
                        [:form.mb-2 {:action "/killteam/specops/addspecop" :method "POST"}
                          [:div.d-flex.mb-2
                            [:h5 "Add Spec Op"]
                            [:button.btn-sm.btn-warning.ms-auto {:role "submit" :disabled (and (-> specops count (> 0)) (->> specops (map :progress) (apply min 6) (> 5)))} "Add"]]
                          [:input {:hidden true :type "number" :readonly true :name "specop" :value uid}]
                          [:input {:hidden true :type "number" :readonly true :name "progress":value 0}]
                          [:div.row
                            [:div.col-sm-5 [:input.form-control {:type "text" :placeholder "Spec Op Name" :name "name" :required true}]]
                            [:div.col-sm-2 [:div.input-group [:span.input-group-text "RP"] [:input.form-control {:type "number" :placeholder "RP" :name "rp" :value 1}]]]
                            [:div.col-sm-5 [:input.form-control {:type "text" :placeholder "Notes" :name "note"}]]]]
                        [:div [:b "Spec Op History"]]
                        [:ul.list-group.list-group-flush
                          (map-indexed (fn [id so]
                            [:li.list-group-item  {:class (case id 0 "list-group-item-primary" (1 2) "" "list-group-item-secondary")}
                              [:div.row
                                [:div.col-sm-8 [:div (:name so) " (" (:rp so) " RP)"]]
                                [:div.col-sm-4
                                  (if (> 6 (:progress so))
                                    [:form.row.row-cols-lg-auto {:action "/killteam/specops/updatespecop" :method "POST"}
                                      [:input {:hidden true :name "specop" :value (:specop so)}]
                                      [:input {:hidden true :name "uid" :value (:uid so)}]
                                      [:div.col-12
                                        [:select.form-control.form-control-sm {:name "progress"}
                                          [:option {:value 0 :selected (= (:progress so) 0)} "Op. #1 0/5"]
                                          [:option {:value 1 :selected (= (:progress so) 1)} "Op. #1 1/5"]
                                          [:option {:value 2 :selected (= (:progress so) 2)} "Op. #1 2/5"]
                                          [:option {:value 3 :selected (= (:progress so) 3)} "Op. #1 3/5"]
                                          [:option {:value 4 :selected (= (:progress so) 4)} "Op. #1 4/5"]
                                          [:option {:value 5 :selected (= (:progress so) 5)} "Op. #1 5/5"]
                                          [:option {:value 6 :selected (= (:progress so) 6)} "Op. #2 Complete"]]]
                                      [:div.col-12 [:button.btn.btn-sm.btn-warning {:type "submit"} "Update"]]]
                                    [:i.ms-auto.fas.fa-check.text-success])]]
                              [:small (:note so)]]) specops)]]
                      [:div#equipment {:class (if (= tab "equipment") "tab-pane fade my-2 show active" "tab-pane fade my-2") :role "tabpanel"} 
                        [:form.mb-2 {:action "/killteam/specops/addequipment" :method "POST"}
                          [:div.d-flex.mb-2
                            [:h5 "Add Equipment"]
                            [:button.btn-sm.btn-warning.ms-auto {:role "submit" :disabled (= 0 (get-ep requisitions equipment))} "Add"]]
                          [:input {:hidden true :name "specop" :type "number" :readonly true :value uid}]
                          [:div.row
                            [:div.col-sm-8 [:input.form-control {:type "text" :placeholder "Equipment Item" :name "name" :required true}]]
                            [:div.col-sm-4 [:div.input-group [:span.input-group-text "EP"] [:input.form-control {:type "number" :name "ep" :value 1}]]]]]
                        [:div [:b "Stash"]]
                        [:ul.list-group.list-group-flush
                          (for [e (->> equipment (map :name) frequencies) :let [ep (->> equipment (filter #(= (:name %) (first e))) first :ep)]]
                            [:li.list-group-item.d-flex.justify-content-between.align-items-center 
                              (str (first e) " (" ep " EP)" )
                              [:span.badge.bg-primary.rounded-pill "x " (second e)]])]
                      ]]
                  ])
              
            ]]]
          ])))