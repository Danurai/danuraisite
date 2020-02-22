(ns danuraisite.scores
  (:require 
    [reagent.core :as r]
    [danuraisite.scoredata :refer [sidata]]))
  
(def app (r/atom nil))

(defn- resetapp []
  (reset! app {
    :players 2
    :spirits (vec (concat (take 2 (->> sidata :spirits (map :name))) (repeat 2 "none")))
    :blight true
    :scenario "none"
    :adversary "none"
    :boards (:boards sidata)
    :advlvl 0
    :score {:win true :invdeck 0 :dahan 10 :blight 2}
    :sets {:core true}}))
    
(def maxdiff 14)
;(+ (->> sidata :scenarios (map :difficulty) (remove nil?) (apply max))
;    (->> sidata :adversaries (map :difficultylevels) (apply concat) (apply max)))

(defn scenadjust [ scen spirits players adv ] 
  0)
;  (prn scen spirits players adv)
;  (prn
;  ;(apply -
;  ;  (map (fn [t]   
;  ;    (count (filter true?
;        (apply conj
;          (map (fn [[k v]] 
;            (case k 
;              :adversaries (contains? (set v) adv)
;              :players (= players v)
;              :spirits (map #(contains? spirits %) v)
;              false)) (:harder scen)))))
;              ; [:harder :easier])))
  

(defn getdiff []
  (let [scen (->> sidata :scenarios   (filter #(= (:name %) (:scenario @app))) first)
        adv  (->> sidata :adversaries (filter #(= (:name %) (:adversary @app))) first)
        advlvl (if (= "Base" (:advlvl @app)) 0 (:advlvl @app))
        spirits (->> @app :spirits (remove #(= "none" %)) set)]
    (+ (:difficulty scen 0)
       (nth (:difficultylevels adv) advlvl)
       (scenadjust scen spirits (:players @app) (:name adv)))))
       
(defn score []
  (+ (/ (-> @app :score :dahan) (:players @app))
     (* -1 (/ (-> @app :score :blight) (:players @app)))
     (if (-> @app :score :win)
         (+ 10 (* (getdiff) 5) (* 2 (-> @app :score :invdeck)))
         (+ (* (getdiff) 2) (- 12 (-> @app :score :invdeck))))))
    
(defn- randomise []
  (let [p (:players @app)
        rnd (rand-int 6)
        sora (cond (= rnd 0) nil (< 0 rnd 3) :scen (< 2 rnd 4) :adv :else :both)
        setfilter (->> @app :sets keys (map name) set)]
    (swap! app assoc
      :spirits   (vec (concat (take p (->> sidata :spirits (filter #(contains? setfilter (:setname %))) shuffle (map :name))) (repeat (- 4 p) "none")))
      :blight    true
      :scenario  (case sora (:scen :both) (->> sidata :scenarios rest shuffle (map :name) first) nil)
      :adversary (case sora (:adv :both) (->> sidata :adversaries rest shuffle (map :name) first) nil)
      :boards    (->> sidata :boards shuffle (take p))
      :advlvl    (-> @app :advlvl (+ (- (rand-int 3) 1)) (max 0) (min 6)))))
    
(defn page [ dom-node ]
  [:div.container-fluid.my-3
    ;[:div (-> @app :advlvl str)]
    [:div.images {:hidden true}
      [:img#boarda {:src "img/boards/boarda.png"}]
      [:img#boardb {:src "img/boards/boardb.png"}]
      [:img#boardc {:src "img/boards/boardc.png"}]
      [:img#boardd {:src "img/boards/boardd.png"}]]
    [:div.row.mb-2
      [:div.col-sm-4
        [:div.d-flex.justify-content-center
          [:div.form-check.form-check-inline
            [:input.form-check-input {
              :type "checkbox" 
              :value (-> @app :sets :promo1) 
              :on-change #(if (-> @app :sets :promo1) (swap! app update :sets dissoc :promo1) (swap! app assoc-in [:sets :promo1] true))}]
            [:label.form-check-label "Promo Pack 1"]]
          [:div.form-check.form-check-inline
            [:input.form-check-input {
              :type "checkbox" 
              :value (-> @app :sets :bandc) 
              :on-change #(if (-> @app :sets :bandc) (swap! app update :sets dissoc :bandc) (swap! app assoc-in [:sets :bandc] true))}]
            [:label.form-check-label "Branch and Claw"]]]]
        [:div.col-sm-4
          [:div.d-flex.justify-content-center.input-group.mb-1 {:style {:display "inline-flex" :flex "auto"}}
            [:label.my-auto.mr-1 "Players:"]
            [:select.form-control.w-auto.mr-2 {
              :value (:players @app) 
              :on-change #(swap! app assoc :players (-> % .-target .-value int))}
              (for [n (range 1 5)] [:option {:key (gensym)} n])]
            [:div [:button.btn.btn-outline-secondary.mr-1 {:on-click #(randomise)} "Randomise"]]]]
        [:div.col-sm-4
          [:div.d-flex.justify-content-center
            [:span.my-auto (str "Difficulty: " (getdiff) "/" maxdiff)]
            [:div.progress.mx-1.border.my-auto {:style {:width "150px"}}
              [:div.progress-bar {:role "" :style {:width (-> (getdiff) (/ maxdiff) (* 100) int (str "%"))}}]]]]]
    [:div.row.mb-2
      [:div.col-sm-6
        [:label "Spirits"]
        (doall (for [n (range (:players @app)) :let [spirits (concat (->> sidata :spirits (map :name)) ["none"])] ]
          [:select.form-control.mb-1 {
            :key (gensym)
            :value (-> @app :spirits (nth n)) 
            :on-change #(swap! app assoc-in [:spirits n] (-> % .-target .-value))}
            (for [spirit spirits]
              [:option {:key (gensym)} spirit])]))]
      [:div.col-sm-6
        [:label "Scenario"]
        [:select.form-control.mb-1 {
          :value (:scenario @app) 
          :on-change #(swap! app assoc :scenario (-> % .-target .-value))}
          (for [scen (->> sidata :scenarios (map :name))]
            [:option {:key (gensym)} scen])]
        [:label "Adversary"]
        [:div.d-flex
          [:select.form-control.mr-1 {
            :value (:adversary @app) 
            :on-change #(swap! app assoc :adversary (-> % .-target .-value))}
            (for [adv (->> sidata :adversaries (map :name))]
              [:option {:key (gensym)} adv])]
          [:label.my-auto.mr-1 "Lvl:"]
          [:select.form-control {
            :style {:width "auto"}
            :value (if (:adversary @app) (:advlvl @app) "Base")
            :on-change #(swap! app assoc :advlvl (-> % .-target .-value int))}
            (for [advlvl (range 7)] [:option {:key (gensym)} (if (= 0 advlvl) "Base" advlvl)])]]]]
    [:div.row.border-top.border-bottom.my-2.py-2
      [:div.col
        [:div.form-inline
          [:label.mr-1.my-auto "Win?"]
          [:input.form-control.mr-2 {
            :type "checkbox" 
            :checked (-> @app :score :win true?) 
            :on-change #(if (-> @app :score :win) (swap! app update :score dissoc :win) (swap! app assoc-in [:score :win] true))}]
          [:label.mr-1 "Invader Deck"]
          [:input.form-control.mr-2 {:type "number" :value (-> @app :score :invdeck) :on-change #(swap! app assoc-in [:score :invdeck] (-> % .-target .-value))}]
          [:label.mr-1 "Dahan"]
          [:input.form-control.mr-2 {:type "number" :value (-> @app :score :dahan) :on-change #(swap! app assoc-in [:score :dahan] (-> % .-target .-value))}]
          [:label.mr-1 "Blight"]
          [:input.form-control.mr-2 {:type "number" :value (-> @app :score :blight) :on-change #(swap! app assoc-in [:score :blight] (-> % .-target .-value))}]
          [:div.h5.ml-auto  "Score: " (score)]
        ]]]
    [:div.row
      [:div.col
        [:div (apply str "Boards: " (->> @app :boards (take (:players @app)) (map #(str "'" (clojure.string/upper-case %) "' "))))]
        [:canvas#drawing.border (if-let [node @dom-node] {:width "800px" :height "450px"})]]]])
    
(defn- draw_canvas [ canvas ]
  (let [ctx (.getContext canvas "2d")
        w 800 ;(.-clientWidth ctx)
        h 450 ;(.-clientHeight ctx)
        boards (:boards @app)
        p (:players @app)]
    (.clearRect ctx 0 0 w h)
    (case p
      1 (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 0)))) 0 0 304 245)
      2 (do 
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 0)))) 100 0 304 245 )
          (.save ctx)
          (.translate ctx 334 430)
          (.rotate ctx (-> Math/PI))
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 1)))) 0 0 304 245)
          (.restore ctx))
      3 (do 
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 0)))) 0 0 304 245 )
          
          (.save ctx)
          (.translate ctx 497 115)
          (.rotate ctx (-> Math/PI (* 2) (/ 3)))
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 1)))) 0 0 304 245 )
          ;(.rect ctx 0 0 304 245)
          ;(.stroke ctx)
          (.restore ctx)
          
          (.save ctx)
          (.translate ctx 149 487)
          (.rotate ctx (-> Math/PI (* 4) (/ 3)))
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 2)))) 0 0 304 245 )
          ;(.rect ctx 0 0 304 245)
          ;(.stroke ctx)
          (.restore ctx))
      (do 
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 0)))) 74 30 304 245 )
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 2)))) 0 213 304 245 )
          
          (.save ctx)
          (.translate ctx 578 248)
          (.rotate ctx (-> Math/PI))
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 1)))) 0 0 304 245 )
          (.restore ctx)
          
          (.save ctx)
          (.translate ctx 504 431)
          (.rotate ctx (-> Math/PI))
          (.drawImage ctx (.getElementById js/document (str "board" (-> @app :boards (nth 3)))) 0 0 304 245 )
          (.restore ctx)
          
          ))))
    
    ;))

(defn canvasclass [ ]
  (let [dom-node (r/atom nil)]
    (r/create-class
     {:component-did-update
        (fn [ this ]
          (draw_canvas (.getElementById js/document "drawing")))
      :component-did-mount
        (fn [ this ]
          (reset! dom-node (r/dom-node this)))
      :reagent-render
        (fn [ ]
          @app
          (page dom-node))})))
    
(resetapp)
(r/render [canvasclass] (.getElementById js/document "app"))