(ns darkfuture.core
  (:require 
    [reagent.core :as r]
    ;[darkfuture.model :refer [phase tileinfo]]
    ))

(def ^:const scale 16)
(def idtotile [:straight :curver :curvel :bendr :bendl :track])

;; track / section / segment | lane
(def orders (r/atom {0 {:acc 20} 1 {:acc 20}}))
(def gs (r/atom {
  :phase 20
  :road {
    :ids [0 0 1 2 0 3 4 2 0] 
    :track (repeat 5 :straight); :curver]
    :loop false}
  :transform {:s 1 :x 50 :y 50}
  :cars [
    {:id 0 :type :ren :hand 4 :acc 20 :dec 30 :maxspeed 100 :driver 1 :skill 3 :speed 0 :loc {:section 0 :seg 0 :lane 2} :clr "red"}
    {:id 1 :type :ren :hand 4 :acc 20 :dec 30 :maxspeed 100 :driver 2 :skill 3 :speed 0 :loc {:section 0 :seg 2 :lane 6} :clr "green"}
  ]
}))

(defn tileinfo 
  "Segments and safty limits for track segments"
  ([ tile y ]
    (case tile
      :curver {:segments (case y (0 1 2) 5 (3 4) 4 3) :outsidesegments (case y (0 1 2) 5 (3 4) 4 3) :limit (case y (0 1) 80 (2 3) 70 (4 5) 60 50) :outsidelimit (case y (0 1) 80 (2 3) 70 (4 5) 60 50):angle 60}
      :curvel {:segments (case y (0 1 2) 3 (3 4) 4 5) :outsidesegments (case y (0 1) 3 (2 3) 4 5)   :limit (case y (0 1) 50 (2 3) 60 (4 5) 70 80) :outsidelimit (case y (0 1) 50 (2 3) 60 (4 5) 70 80):angle 60}
      :bendr  {:segments (case y (0 1 2) 4 (3 4) 3 2) :outsidesegments (case y (0 1 2) 4 (3 4) 3 2) :limit (case y (0 1) 60 (2 3) 50 (4 5) 40 30) :outsidelimit (case y (0 1) 60 (2 3) 50 (4 5) 40 30):angle 90}
      :bendl  {:segments (case y (0 1 2) 2 (3 4) 3 4) :outsidesegments (case y (0 1) 2 (2 3) 3 4)   :limit (case y (0 1) 30 (2 3) 40 (4 5) 50 60) :outsidelimit (case y (0 1) 30 (2 3) 40 (4 5) 50 60):angle 90}
      :line   {:segments 1 :outsidesegments 1 :limit 80}
      {:outsidesegments 3 :segments 3 :limit 80}))
  ([ tile ]
    (tileinfo tile 0)))

(defn- degtorads [ deg ] (-> deg (* Math/PI) (/ 180)))

(defn dot [ ctx ] (.beginPath ctx) (set! (.-fillStyle ctx) "rgba(255,0,0,0.4)") (.arc ctx 0 0 10 0 (* 2 Math.PI)) (.fill ctx))

(defn drawcar [ ctx car ]
  (let [img (js/Image.) path (js/Path2D.) w (* scale 4) h (* scale 2)]
    (set! (.-src img) (str "/img/ren" (-> car :id inc) ".png") ) 
    (.rect path 0 0 w h)
    (when (.isPointInPath ctx path (-> @gs :x) (-> @gs :y) )
      (set! (.-fillStyle ctx) "darkorange")
      (.fill ctx path))
    (.drawImage ctx img 0 0 w h)))

(defn straight [ ctx {:keys [id type]} cars ]
  (let [segments (-> type tileinfo :segments)]
    (.beginPath ctx)
    (doseq [seg (range segments) :let [w (apply * [scale seg 4]) h (* scale 8)]] 
      (.moveTo ctx w 0)
      (.lineTo ctx w h))
    (doseq [lane (range 9) :let [w (apply * [scale segments 4]) h (* scale lane)]] 
      (.moveTo ctx 0 h)
      (.lineTo ctx w h))
    (.stroke ctx)
    (doseq [car cars]
      (let [{:keys [seg lane]} (:loc car)]
        (.save ctx)
        (.translate ctx (apply * [4 seg scale]) (* lane scale))
        (drawcar ctx car)
        (.restore ctx)))
    ; move to startof next section
    (.translate ctx (apply * [scale segments 4]) 0)))

(defn turn-r [ ctx {:keys [id type]} cars]
  (let [rads (degtorads (case type :curver 60 :bendr 90)) radius (case type :curver 19 :bendr 13)]
    (doseq [lane (range 9) :let [{:keys [segments limit]} (tileinfo type lane)]]
      (.beginPath ctx)
      (.arc ctx 0 (* scale radius) (* scale (- radius lane)) (degtorads 270) (+ (degtorads 270) rads) )
      (.stroke ctx)
      (when (< lane 8)
        (.save ctx)
        (.rotate ctx (/ Math/PI 2))
        (set! (.-textAlign ctx) "center")
        (.translate ctx (* scale (+ 0.5 lane)) 0)
        (.strokeText ctx limit 0 0)
        (.restore ctx) 
        (doseq [seg (range segments)]
          (.save ctx)
          (.translate ctx 0 (* radius scale))
          (.rotate ctx (* seg (/ rads segments)))
          (.beginPath ctx)
          (.moveTo ctx 0 (* -1 scale (- radius lane 1)))
          (.lineTo ctx 0 (* -1 scale (- radius lane)))
          (.stroke ctx)
          (.restore ctx))))
    (doseq [car cars]
      (let [{:keys [section seg lane]} (:loc car) {:keys [segments]} (tileinfo type lane)]
        (.save ctx)
        (.translate ctx 0 (* radius scale))
        (.rotate ctx  (* (+ seg .5) (/ rads segments)))
        (.translate ctx 0 (apply * [-1 scale (- (dec radius) lane)]))
        (.translate ctx (* -2 scale) (* -1 scale))
        (drawcar ctx car)
        (.restore ctx)))
      
    ; avoid trig
      (case type 
        :curver (.translate ctx (* (* scale 19) (/ (Math.sqrt 3) 2)) (* scale (/ 19 2)))
        :bendr  (.translate ctx (* scale radius) (* scale radius)))
      (.rotate ctx rads)))

(defn turn-l [ ctx {:keys [id type]} cars]
  (let [deg (-> type tileinfo :angle) rads (degtorads deg) radius (case type :curvel 11 :bendl 5)]
    (doseq [lane (range 9) :let [{:keys [segments]} (tileinfo type lane)]]
      (.beginPath ctx)
      (.arc ctx 0 (apply * [-1 scale radius]) (* scale (+ radius lane)) (degtorads 90) (- (degtorads 90) rads) -1)
      (.stroke ctx)
      (if (< lane 8) (doseq [seg (range segments)]
        (.save ctx)
        (.translate ctx 0 (apply * [-1 radius scale]))
        (.rotate ctx (apply * [-1 seg (/ rads segments)]))
        (.beginPath ctx)
        (.moveTo ctx 0 (* scale (+ radius lane 1)))
        (.lineTo ctx 0 (* scale (+ radius lane)))
        (.stroke ctx)
        (.restore ctx))))
    (doseq [car cars]
      (let [{:keys [section seg lane]} (:loc car) {:keys [outsidesegments]} (tileinfo type lane)]
        (.save ctx)
        (.translate ctx 0 (apply * [-1 radius scale]))
        (.rotate ctx  (apply * [-1 (+ seg .5) (/ rads outsidesegments)]))
        (.translate ctx 0 (* scale (+ (inc radius) lane)))
        (.translate ctx (* -2 scale) (* -1 scale))
        (drawcar ctx car)
        (.restore ctx)))
      
    ; avoid trig
      (case type 
        :curvel (.translate ctx (* (* scale radius) (/ (Math.sqrt 3) 2)) (apply * [-1 scale (/ radius 2)]))
        :bendl  (.translate ctx (* scale radius) (apply * [-1 scale radius])))
      (.rotate ctx (* -1 rads))))




(defn- draw! [ canvas ]
  (let [ctx (.getContext canvas "2d")
        w   (.-clientWidth  canvas) 
        h   (.-clientHeight canvas)]

    (.setTransform ctx 1 0 0 1 0 0)
    (.clearRect ctx 0 0 w h)

    (let [{:keys [s x y]} (:transform @gs)] (.setTransform ctx s 0 0 s x y))
    (set! (.-strokeStyle ctx) "rgb(100,100,100)")
  
    (doseq [section (->> @gs :road :track (map-indexed #(hash-map :id %1 :type %2)))
            :let [cars (->> @gs :cars (filter #(= (:id section) (-> % :loc :section))))]]
      ;(dot ctx)
      (case (:type section)
        :curver   (turn-r ctx section cars)
        :curvel   (turn-l ctx section cars)
        :bendr    (turn-r ctx section cars)
        :bendl    (turn-l ctx section cars)
        (straight ctx section cars)))))

(defn mousemove [ evt ]
  (let [ele  (.-target evt)
        x    (- (.-pageX evt) (.-offsetLeft ele))
        y    (- (.-pageY evt) (.-offsetTop ele))]
  (swap! gs assoc :x x :y y)))

(defn div-with-canvas []
  (let [dom-node (r/atom nil)]
    (r/create-class {
      :component-did-update
        (fn [this] 
          (draw! (.-firstChild @dom-node)))
      
      :component-did-mount  
        (fn [this] 
          (reset! dom-node (r/dom-node this))
          ;; pre-load images https://stackoverflow.com/questions/42615556/how-to-preload-images-in-react-js
          (set! (.-src (js/Image.)) "/img/ren1.png")
          (set! (.-src (js/Image.)) "/img/ren2.png"))
      
      :reagent-render
        (fn []
          @gs
          [:div
            [:canvas#drawing.d-block 
              (if-let [node @dom-node] {
                ;:data-dom (-> @dom-node .-target str)
                :width (.-clientWidth node)
                :height "300px" ;(.-clientHeight node)
                :on-mouse-move mousemove
                :on-click #(swap! gs dissoc :selected)})]])})))


(defn logcommand [ id cmd ] (swap! orders assoc id cmd))

(defn- order-button [ id order label selected ]
  [:button.btn.btn-secondary {
      :class (if (= selected order) "active")
      :on-click #(logcommand id order)}
    label])

(defn dashboards [ cars ]
  [:div
    (doall (for [car cars :let [{:keys [id acc dec hand skill]} car selectedorder (get @orders id {})]]
      [:div.d-flex.my-2 {:key (gensym)}
        [:div.px-2 {:style {:border (str "2px solid " (:clr car) )}} (str (-> car :type) (:id car))]
        [:div.border.border-dark.bg-secondary.text-light.px-2.me-2 (:speed car)]
        [:div
          [:i.fas.fa-car.me-2] [:span.me-2 hand]
          [:i.fas.fa-user.me-2] [:span.me-2 skill]]
        [:div.btn-group.btn-group-sm.ms-1
          (order-button id {:acc acc} (str "Acc " acc) selectedorder)
          (order-button id {:dec dec} (str "Dec " dec) selectedorder)
          (order-button id {:drift :r} "Drift R" selectedorder)
          (order-button id {:drift :l} "Drift L" selectedorder)
          (order-button id {} "None" selectedorder)
          ]
        ;[:div (str car)]
        ]))])

(defn- domove [ car phase road ]
  (let [{:keys [speed]} car 
        {:keys [section seg lane]} (:loc car) 
        track (:track road)
        {:keys [outsidesegments]} (-> track (get section) (tileinfo lane))]
    (prn speed phase)
    (if (>= speed (- phase 20))
        (if (= (inc seg) outsidesegments)
            (-> car (assoc-in [:loc :seg] 0) (update-in [:loc :section] #(-> % inc (mod (count track)))))
            (-> car (update-in [:loc :seg] inc)))
        car)))
(defn hazardcheck [ car oldlane newlane ] 
  car)

(defn doaction [ car phase action road ]
  (if (>= (:speed car) (- phase 20))
    (let [newspeed (-> car :speed (+ (:acc action)) (min (:maxspeed car)) (- (:dec action)) (max 0) )
          laneswap (case (:drift action) :r 1 :l -1 0)
          newlane  (-> car :loc :lane (+ laneswap) (min 6) (max 0))]  ; can't cut up car which hasn't moved.
      ; check hazard - drift or corner
      ; do check & apply results
      (-> car
          (assoc :speed newspeed)
          (domove phase road)
          (assoc-in [:loc :lane] newlane)
          ))
    car))

(defn movecars [ {:keys [cars phase road]} ]
  (map 
    (fn [ car ]
      (let [action (get @orders (:id car))]
        (-> car 
            (doaction phase action road))
        )) (sort-by :speed > cars)))

(defn updatephase [ gs ]
  (let [phase (:phase gs) maxphase (->> gs :cars (map :speed) (apply max)) ]
    (assoc gs :phase (if (>= phase maxphase) 20 (+ phase 20)))))


(defn phase [ gs ]
    (-> gs 
        (assoc :cars (movecars gs))
        updatephase))
    
(defn page [] 
  (swap! gs assoc :cars 
    (map #(let [imgobj (js/Image.)]
      (assoc % :img imgobj)) (:cars @gs)))
  (fn []
    [:div
      [:div.container
        [:div
          [:input {:type "range" :min "1" :max "15" :value (-> @gs :transform :s (* 10)) :on-change #(swap! gs assoc-in [:transform :s] (-> % .-target .-value (/ 10)))}]]
        [dashboards (:cars @gs)]
        [:div.d-flex
          [:div.h4.me-3 (-> @gs :phase (/ 20) Math.ceil) ]
          [:button.btn.btn-secondary  {:disabled (< (count @orders) (-> @gs :cars count) ) :on-click #(reset! gs (phase @gs))} "Phase!"]]]
      [div-with-canvas]]))

(r/render [page] (.getElementById js/document "app"))
(.addEventListener js/window "resize" #(swap! gs assoc :window-width (.-innerWidth js/window)))