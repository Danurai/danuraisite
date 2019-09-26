(ns kasei.core 
  (:require 
    [reagent.core :as r]
    [kasei.model :refer [appstate]]
    [kasei.hexes :as hex]))
  
(enable-console-print!)  

(def ^:const _origin  {:x 225 :y 225})
(def ^:const _hexsize 25)
(def ^:const _gridsize 4)

(defn resetstate! []
  (reset! appstate {:board {
                    :size 4 
                    :map {} 
                    :restricted {
                      [1 -4] :ocean [3 -4] :ocean [4 -4] :ocean 
                      [4 -3] :ocean [4 -1] :ocean 
                      [-2 0] :city [-1 0] :ocean [0 0] :ocean [1 0] :ocean
                      [1 1] :ocean [2 1] :ocean [3 1] :ocean
                      [0 4] :ocean
                      [-3 -3] :city [-1 -5] :city}}
                  :gen 1
                  :oxygen 0
                  :heat -30
                  :oceans 0
                  :mouseqr nil
                  :p1 {
                    :corp "Basic"
                    :tr 20
                    :prod {:mb 1  :steel 1 :tit 1 :plant 1 :energy 1 :heat 1}
                    :bank {:mb 48 :steel 0 :tit 0 :plant 0 :energy 0 :heat 0}
                    }}))
     
(defn- mousemove [ evt ]
  (let [target (.-target evt)
        de     (.-documentElement js/document)
        mx     (-> (.-clientX evt) (- (.-offsetLeft target)) (- (.-offsetLeft (.-offsetParent target))) (+ (.-scrollLeft de)))
        my     (-> (.-clientY evt) (- (.-offsetTop  target)) (+ (.-scrollTop  de)))
        cell   (hex/pixel_to_pointy_hex _origin [mx my] _hexsize)]
    (swap! appstate assoc :mouseqr cell
                        :mousexy [mx my])))
;      
;(defn- mouseclick [ evt ]
;  (let [[x y] (:cell @appstate)]
;    (if (and (< -1 x (-> @appstate :board :width)) (< -1 y (-> @appstate :board :height)))
;        (swap! appstate assoc :orig (:cell @appstate)
;                              :paths (get_paths (:orig @appstate) (:cell @appstate))
;                              :path_home nil))))

(defn- draw_grid_axial [ ctx origin hexsize dist ]
  (let [a (- 0 dist) b (inc dist)]
    (doseq [q (range a b) r (range a b)]
      (if (>= dist (Math/abs (+ q r)))
          (hex/draw_pointy_hex ctx origin hexsize [q r])))))
          
(defn- draw_kasei [ ctx origin hexsize gridsize ]
  (let [r (-> hexsize
             (* 2) ; height
             (* 3)
             (/ 4) ; hex height
             (* 4.9))]
    (.beginPath ctx)
    (set! (.-fillStyle ctx) "rgb(190,0,0)")
    (.arc ctx (:x origin) (:y origin) r 0 (* 2 Math/PI))
    (.fill ctx)))
  
(defn- draw_image [ ctx image ]
  (let [ele (.getElementById js/document "kaseibg")]
    (.drawImage ctx ele 0 0 450 450)
  ))
;  (let [img (.createElement js/document "img")
;       ele (set! (.-src img) "/img/OSIRIS_Mars_true_color.jpg")]
;    (set! (.-onload ele)
;      (.drawImage 
;        ctx 
;        img
;        0 0 450 450))))
      
(defn draw_page [ canvas ]
  (let [ctx (.getContext canvas "2d")
        w   (.-clientWidth  canvas) 
        h   (.-clientHeight canvas)]
    (.clearRect ctx 0 0 w h)
    (draw_image ctx "OSIRIS_Mars_true_color.jpg")
    ;(draw_kasei ctx _origin _hexsize _gridsize)
    (draw_grid_axial ctx _origin _hexsize _gridsize)
    ;phobos and ganymede
    (hex/draw_pointy_hex ctx _origin _hexsize [-3 -3])
    (hex/draw_pointy_hex ctx _origin _hexsize [-1 -5])
    (if (and 
          (:mousexy @appstate)
          (< (-> @appstate :board :size) (hex/hex_distance [0 0] (:mouseqr @appstate))) ;board
          (not-any? #(= % (-> @appstate :mouseqr)) (->> @appstate :board :restricted keys))) ; captures phobos and ganymede
        (hex/draw_pointy_hex ctx (zipmap [:x :y] (:mousexy @appstate)) _hexsize [0 0]))
    
    ))
    
(defn- action-button [ action title cb ]
  [:button.btn.btn-light {
    :class (if (= (:action @appstate) action) "active")
    :on-click (if cb cb #(swap! appstate assoc :action action))
    } 
    title])
    
(defn page [ dom-node ]
  [:div.container-fluid.my-1
    [:img#kaseibg {:src "/img/OSIRIS_Mars_true_color.jpg" :hidden true}]
    [:div.row
      [:div.col-sm-7
        [:div.row
          [:div.col-sm-9
            [:div.row
              [:canvas#drawing.border 
                (if-let [node @dom-node] {
                  :width "450px" 
                  :height "450px"
                  :on-mouse-move mousemove
                  ;:on-click      mouseclick
                  :on-mouse-out  #(swap! appstate dissoc :mousexy)})]]]
          [:div.col-sm-3
            [:div.row
              [:div.btn-group-vertical.btn-grp-sm
                (action-button :sell     "Sell Patents (+X)" nil)
                (action-button :power    "Power Plant (11)" 
                  (fn [] 
                    (when (<= 11 (-> @appstate :p1 :bank :mb))
                      (swap! appstate update-in [:p1 :prod :energy] inc)
                      (swap! appstate update-in [:p1 :bank :mb] - 11))))
                (action-button :asteroid "Asteroid (14)" 
                  (fn []
                    (when (and 
                           (> 8 (:heat @appstate))
                           (<= 14 (-> @appstate :p1 :bank :mb)))
                      (swap! appstate update :heat + 2)
                      (swap! appstate update-in [:p1 :bank :mb] - 14)
                      (swap! appstate update-in [:p1 :tr] inc))))
                (action-button :aquifer  "Aquifer (18)" nil)
                (action-button :greenery "Greenery (23)" nil)
                (action-button :city     "City (25)" nil)
                (action-button :nextgen  "Next Generation"
                  (fn []
                    (swap! appstate update :gen inc)
                    (swap! appstate update-in [:p1 :bank :heat] + (-> @appstate :p1 :bank :energy))
                    (swap! appstate assoc-in [:p1 :bank :energy] 0)
                    (swap! appstate update-in [:p1 :bank :mb] + (-> @appstate :p1 :tr))
                    (doseq [r [:mb :steel :tit :plant :energy :heat]]
                      (swap! appstate update-in [:p1 :bank r] + (-> @appstate :p1 :prod r)))
                      
                    ))
                (action-button :reset "Reset State" #(resetstate!))]]]]]
      
      [:div.col-sm-5
        [:div.row.mb-2
          [:div.h4.mr-3 (str "Gen " (:gen @appstate))]
          [:div.mr-3 (str "O2: " (:oxygen @appstate) "/14")]
          [:div.mr-3 (str "Temp: " (:heat @appstate) "/8")]
          [:div.mr-3 (str "Oceans: " (:oceans @appstate) "/9")]
          [:div.h4 (str "TR: " (-> @appstate :p1 :tr))]]
        [:div.row.mb-2
          (let [p (:p1 @appstate)]
            [:table.table.table-sm
              [:thead [:tr [:th "Resource"][:th "Production"][:th "Amount"]]]
              [:tbody
                (for [[title k] [["M€" :mb]["Steel" :steel] ["Titanium" :tit]["Plants" :plant]["Energy" :energy]["Heat" :heat]]]
                  ^{:key (name k)}[:tr [:td title][:td (-> p :prod k)][:td (-> p :bank k)]])]])]
        [:div.row
          [:div (str @appstate)]]]]])
    
(defn canvasclass [ ]
  (let [dom-node (r/atom nil)]
    (r/create-class
     {:component-did-update
        (fn [ this ]
          (draw_page (.getElementById js/document "drawing")))
      :component-did-mount
        (fn [ this ]
          (reset! dom-node (r/dom-node this)))
      :reagent-render
        (fn [ ]
          @appstate
          (page dom-node))})))

(resetstate!)
(r/render-component [canvasclass] (.getElementById js/document "kasei"))