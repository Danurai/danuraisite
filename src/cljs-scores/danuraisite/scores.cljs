(ns danuraisite.scores
  (:require 
    [reagent.core :as r]
    [danuraisite.scoredata :refer [sidata]]))
  
(def app (r/atom nil))

(defn- resetapp []
  (reset! app {
    :players 2
    :spirits (vec (concat (take 2 (->> sidata :spirits (map :name))) (repeat 2 "none")))
    ;:blight true
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
      [:h4.text-center "Spirit Island Score Log"]]
    [:h5 "Setup"]
    [:div.row.mb-2
      [:div.col-sm-4
        [:div.d-flex.justify-content-center.input-group.mb-1 {:style {:display "inline-flex" :flex "auto"}}
          [:label.my-auto.me-1 "Players:"]
          [:select.form-control.w-auto.me-2 {
            :value (:players @app) 
            :on-change #(swap! app assoc :players (-> % .-target .-value int))}
            (for [n (range 1 5)] [:option {:key (gensym)} n])]
          [:div [:button.btn.btn-outline-secondary.me-1 {:on-click #(randomise)} "Randomise"]]]]
      [:div.col-sm-4
        [:div.d-flex.justify-content-center
          [:span.me-2 "Expansions:"]
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
        [:div.d-flex.justify-content-center
          [:span.my-auto (str "Difficulty: " (getdiff) "/" maxdiff)]
          [:div.progress.mx-1.border.my-auto {:style {:width "150px"}}
            [:div.progress-bar {:role "" :style {:width (-> (getdiff) (/ maxdiff) (* 100) int (str "%"))}}]]]]]
    [:div.row.mb-3
      [:div.col-sm-6
        [:label "Spirits"]
        (doall (for [n (range (:players @app)) :let [spirits (concat (->> sidata :spirits (map :name)) ["none"])] ]
          [:select.form-control.mb-1 {
            :key (gensym)
            :value (-> @app :spirits (nth n)) 
            :on-change #(swap! app assoc-in [:spirits n] (-> % .-target .-value))}
            (for [spirit spirits]
              [:option {:key (gensym)} spirit])]))
        [:div.d-flex.justify-content-around
          [:button.btn.btn-primary {:type "button" :data-bs-toggle "modal" :data-bs-target "#mapmodal"} 
            (apply str "Show Boards: " (->> @app :boards (take (:players @app)) (map #(str "'" (clojure.string/upper-case %) "' "))))]]]
      [:div.col-sm-6
        [:label "Scenario"]
        [:select.form-control.mb-1 {
          :value (:scenario @app "None") 
          :on-change #(swap! app assoc :scenario (-> % .-target .-value))}
          (for [scen (->> sidata :scenarios (map :name))]
            [:option {:key (gensym)} scen])]
        [:label "Adversary"]
        [:div.d-flex
          [:select.form-control.me-1 {
            :value (:adversary @app) 
            :on-change #(swap! app assoc :adversary (-> % .-target .-value))}
            (for [adv (->> sidata :adversaries (map :name))]
              [:option {:key (gensym)} adv])]
          [:label.my-auto.me-1 "Lvl:"]
          [:select.form-control {
            :style {:width "auto"}
            :value (:advlvl @app "Base")
            :on-change #(swap! app assoc :advlvl (-> % .-target .-value int))}
            (for [advlvl (range 7)] [:option {:key (gensym)} (if (= 0 advlvl) "Base" advlvl)])]]]]
    [:div.row.border-top.my-2.py-2
      [:div.col
        [:h5 "Board State at end of Game"]
        [:div.d-flex.justify-content-between
          [:div.d-flex
            [:label.me-1.my-auto {:style {:white-space "nowrap"}} "Invader Deck"]
            [:input.form-control {:type "number" :value (-> @app :score :invdeck) :on-change #(swap! app assoc-in [:score :invdeck] (-> % .-target .-value))}]]
          [:div.d-flex
            [:label.me-1.my-auto "Dahan"]
            [:input.form-control {:type "number" :value (-> @app :score :dahan) :on-change #(swap! app assoc-in [:score :dahan] (-> % .-target .-value))}]]
          [:div.d-flex
            [:label.me-1.my-auto "Blight"]
            [:input.form-control {:type "number" :value (-> @app :score :blight) :on-change #(swap! app assoc-in [:score :blight] (-> % .-target .-value))}]]
          [:div.form-check.me-2.my-auto 
            [:input.form-check-input {
              :type "checkbox" 
              :checked (-> @app :score :win true?) 
              :on-change #(if (-> @app :score :win) (swap! app update :score dissoc :win) (swap! app assoc-in [:score :win] true))}]
            [:label.form-check-label "Win?"]]
          [:h4.my-auto.me-3  "Score: " (score)]
        ]]]
    [:div.row.border-bottom..pb-2.mb-2
      [:div.col
        (let [savedata (assoc (select-keys @app [:players :adversary :scenario :advlvl])
                        :spirits (clojure.string/join ", " (take (:players @app) (:spirits @app)))
                        :boards (clojure.string/join ", " (take (:players @app) (:boards @app)))
                        :win (-> @app :score :win)
                        :invdeck (-> @app :score :invdeck)
                        :dahan (-> @app :score :dahan)
                        :blight (-> @app :score :blight)
                        :score (score)
                        :difficulty (getdiff)
          )] 
          [:form
            [:button.btn.btn-dark {
              :on-click #(.post (js* "$") "/scores" (clj->js savedata))}
              "Save Game Result"]])]]])
    
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