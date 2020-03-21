(ns danuraisite.rklotrview
  (:require
    [reagent.core :as r]
    [danuraisite.rklotrmodel :as model]
    [danuraisite.rklotrmarkdown :refer [md mdicons]]))
    
  
(defn- greyshade [ c ]
  (let [rgb (- 255 (* 15 c))]
    (str "rgb(" rgb "," rgb "," rgb ")")))
    
(defn corruption []
  [:div#corruption.d-flex.mb-1
    [:table
      [:tbody [:tr
        (doall (for [c (range 16)]
          ^{:key (gensym)}[:td {:style {:background-color (greyshade c)}} 
            (for [p (->> @model/app-state :players (filter #(= (:corruption %) c)))]
              ^{:key (gensym)}[:i.fas.fa-walking.mr-1.fa-lg {:class (if (:ringbearer p) "ringbearer") :style {:color (:clr p)} :title (:name p)}])
            (if (= (-> @model/app-state :sauron) c) [:i.fas.fa-eye.fa-2x.text-danger])
            [:small.text-muted {:style {:position "absolute" :top "1px" :left "1px" }} c]]))]]]])
      
(defn events []
  (let [phase (:phase @model/app-state)
        active-step (get (:steps phase) (:step phase))]
    [:div.p-1
      [:div.float-right.clearfix
        [:button.btn.btn-outline-secondary.btn-sm.mr-2 {:on-click #(model/prev-phase! phase)} "Back"]
        [:button.btn.btn-outline-secondary.btn-sm {:on-click #(model/next-phase! phase)} "Next"]]
      [:h5.text-center (:name phase)]
      [:div.row.mb-1
        (for [s (:steps phase) :let [step (s model/phase-steps)]]
          [:div.col.border.mx-1 {
            :key (gensym) 
            :class (cond (= s active-step) "border-warning" (contains? (:stepsdone phase) s) "border-success" :else "text-muted")}
            [:div.text-center [:b (:title step)]]
            [:div.text-center (md (:desc step ""))]])]]))
      
    
(defn activestep [ step ]
  [:div.row.p-3.border.mb-2
    [:div
      [:div [:b.mr-1 (:title step)]]
      [:div.mb-1 (md (:desc step ""))]
      (if (= (:type step) :number)
          [:div
            [:div.slidercontainer.d-flex
              [:input.slider.mr-2 {
                :type "range"
                :value (-> @model/app-state :activestep :val) :min (:min step) :max (:max step)
                :on-change #(swap! model/app-state assoc-in [:activestep :val] (.. % -target -value))}]
              [:span.mr-2 (-> @model/app-state :activestep :val)]
              [:button.btn.btn-primary.btn-sm {
                :on-click (fn [] 
                  (if-let [step-effect (:effect step)] 
                          (step-effect model/app-state nil (:val step) (:target step)))
                  (model/next-step!))
                } "OK"]]]
          [:div.d-flex.justify-content-center.mb-2
            (for [o (:options step)]
              ^{:key (gensym)}[:button.btn.btn-primary.btn-sm.mr-1 {
                :on-click (fn [] 
                  (if-let [step-effect (:effect o)]
                          (step-effect model/app-state nil (:val o) (:target step)))
                  (model/next-step!))}
                (:title o)])])]])
                
(defn rollscreen [ event ]
  [:div.row.p-3.border.mb-2
    (if (nil? (:result event))
      [:button.btn.btn-outline-secondary {:on-click #(model/rolldie!) } "Roll"]
      [:div 
        [:span.mr-2 (-> event :result :msg)]
        [:button.btn.btn-outline-secondary {:on-click #(model/applydieresult! event) } "Apply"]])])
        
(defn distributescreen [ event ]
  [:div.row.p-3.border.mb-2
    "Distribute"
    [:button.btn.btn-outline-secondary {:on-click #((model/next-event!) (model/next-step!))} "Next"]])
    
                
(defn activeevent [ event ]
  (case (:event event)
    :roll (rollscreen event)
    :distribute (distributescreen event)
    "Unknown Event"))
    
(def card-icon {
  :fellowship [:i.fas.fa-hands-helping {:key (gensym) :title "Fellowship"}]
  :fight [:i.ra.ra-crossed-axes {:key (gensym) :title "Fighting"}]
  :hide [:i.fas.fa-tree {:key (gensym) :title "Hiding"}]
  :travel [:i.fas.fa-shoe-prints {:key (gensym) :title "Travelling"}]
  :wild [:i.far.fa-star {:key (gensym) :title "Wild Card"}]})
  
(defn players []
  [:div.d-flex.mb-3
    (doall (for [p (:players @model/app-state)]
      ^{:key (gensym)}[:div.col 
        [:div.mb-2
          (if (:ringbearer p) [:i.fas.fa-ring.mr-2.text-warning])
          (:name p) ]
        [:div.row
          (for [c (->> @model/app-state :cards (filter #(= (:loc %) (:plyr p))) (sort-by :id))]
            ^{:key (gensym)}[:span.p-2.border.mr-1.mb-1.resource-card {:style {:background-color (:clr c)}}
              (for [icon (:icons c)] (card-icon icon) )])]]))])
    
(defn page []
  (model/init!)
  (fn []
    [:div.container.my-3
      ;[:div (md "Some _emphasis_ and *em* and __bold__ and **bold** and icon [roll] test [fellowship] [wild] [travel] [fight] [hide]")]
      
      [corruption]
      [events]
      (if (empty? (:eventq @model/app-state))
        [activestep (:activestep @model/app-state)]
        [activeevent (-> @model/app-state :eventq first)])
      [players]
      [:div
        [:b.mr-1 "Cards:"]
        [:span.mr-1 (str "deck: " (->> @model/app-state :cards (filter #(= (:loc %) :deck)) count))]
        [:span.mr-1 (str "p1: " (->> @model/app-state :cards (filter #(= (:loc %) 1)) count))]
        [:span.mr-1 (str "p2: " (->> @model/app-state :cards (filter #(= (:loc %) 2)) count))]
        [:span.mr-1 (str "p3: " (->> @model/app-state :cards (filter #(= (:loc %) 3)) count))]
        [:span.mr-1 (str "p4: " (->> @model/app-state :cards (filter #(= (:loc %) 4)) count))]
        [:span.mr-1 (str "p5: " (->> @model/app-state :cards (filter #(= (:loc %) 5)) count))]]
      [:div 
        [:button.btn {:on-click #(model/draw-card! model/app-state {:plyr 1})} "P1 Draw"]
        [:button.btn {:on-click #(model/draw-card! model/app-state {:plyr 2})} "P2 Draw"]]
      [:div (str (dissoc @model/app-state :cards))]
      [:div [:button.btn.btn-secondary {:on-click #(model/init!)} "Reset"]]
  ]))
    
    

; Drag and Drop example - Javascript
;[:div
;  [:p "Drag the W3Schools image into the rectangle"]
;  [:div#div1 {
;    :style {:width 350 :height 70 :padding 10 :border "1px solid #aaaaaa"}
;    :on-drop (fn [ev]
;      (.preventDefault ev)
;      (.appendChild (.-target ev) (.getElementById js/document (-> ev .-dataTransfer (.getData "text")))))
;    :on-drag-over #(.preventDefault %)}]
;  [:br]
;  [:img#drag1 {
;    :style {:width 336 :height 69}
;    :src "https://www.w3schools.com/html/img_logo.gif" 
;    :draggable true 
;    :on-drag-start #(.setData (.-dataTransfer %) "text" (.. % -target -id)) ; ev.dataTransfer.setData("text", ev.target.id);
;     }]]