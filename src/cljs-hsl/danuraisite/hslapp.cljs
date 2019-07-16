(ns danuraisite.hslapp
  (:require
    [reagent.core :as r]
    [goog.color :as color]
    [goog.string :as gstring]))
    
(enable-console-print!)
    
(def hslvals (r/atom {:h (rand-int 360) :s 100 :l 50 :c 1 :h1 0 :m 0 :rgb [255 0 0]}))
(def samples (r/atom nil))

(defn- fontcolour [ hex ]
  (if (> 0.5 (/ (reduce + (map #(* %1 %2)[0.299 0.587 0.114] (goog.color/hexToRgb hex))) 255))
     "white" "black"))

(defn hsltorgb 
"Convert hsl array of [Hue (0-360), Saturation (0-1) Luminance (0-1)] to rgb array of int values.
Intermediary Values:
  h1 - Hue identifier on RGB cube
  c - Chroma - Primary component of colour
  x - Second Largest component of colour
  m - Lightness"
  [ hsl ]
  (let [[h s l] hsl
        h1 (/ h 60)
        c (* s (- 1 (Math/abs (dec (* 2 l)))))
        x (* c (- 1 (Math/abs (dec (mod h1 2)))))
        m (- l (/ c 2))]
    (map #(-> % (+ m) (* 255) int)
      (cond 
        (<= h1 1) [c x 0]
        (<= h1 2) [x c 0]
        (<= h1 3) [0 c x]
        (<= h1 4) [0 x c]
        (<= h1 5) [x 0 c]
        (<= h1 6) [c 0 x]
        :else [0 0 0]))))
 
(defn hsltohex [ hsl ]
  (->> hsl 
       hsltorgb
       (apply color/rgbToHex)
       clojure.string/upper-case))

(defn rand-hsl 
  ([hmin hmax smin smax lmin lmax]
    [(mod (+ hmin (rand-int (- hmax hmin))) 360) (+ smin (rand (- smax smin))) (+ lmin (rand (- lmax lmin)))])
  ([hmin hmax]
    (rand-hsl hmin hmax 0 1 0 1))
  ([]
    (rand-hsl 0 360)))
    
(defn- hueparams [ huename ]
  (case huename
   "Yellow"  [ 20  70 0.25 1 0.2 0.8]
   "Green"   [ 70 170 0.25 1 0.2 0.8]
   "Blue"    [170 270 0.25 1 0.2 0.8]
   "Purple"  [270 320 0.25 1 0.2 0.8]
   "Red"     [320 400 0.25 1 0.2 0.8]
   []))

(defn rand-hsl-hue [ huename ]
  (hsltohex (apply rand-hsl (hueparams huename))))
   
(defn update-rand-hsl [ huename ]
  (reset! samples (hash-map
                  :name huename
                  :colours (repeatedly 20 #(rand-hsl-hue huename)))))
      
(defn convert [ hslint ]
  (let [hsl [(:h @hslvals) (/ (:s @hslvals) 100) (/ (:l @hslvals) 100)]
        rgb (hsltorgb hsl)]
    (swap! hslvals assoc :rgb rgb :hex (apply color/rgbToHex rgb))))
    
(defn Page []
  [:div.row
    [:style 
      (str 
      "input.hue-range::-webkit-slider-thumb {
          background-color: " (:hex @hslvals) " important!;
        }
        input.sat-range::-webkit-slider-runnable-track {
          background-image: linear-gradient(90deg, " (hsltohex [(:h @hslvals) 0 (/ (:l @hslvals) 100)]) "," (hsltohex [(:h @hslvals) 1 (/ (:l @hslvals) 100)]) ");
        } 
        input.lum-range::-webkit-slider-runnable-track {
          background-image: linear-gradient(90deg,black," (hsltohex [(:h @hslvals) (/ (:s @hslvals) 100) 0.5]) ",white);
        })")]
    [:div.col-sm-6
      [:form
        [:label "Hue 0-360"]
        [:input.form-control-range.hue-range.w-100 {
          :type "range" :min "0" :max "360" :value (:h @hslvals)
          :on-change (fn [e] (swap! hslvals assoc :h (.. e -target -value))(convert @hslvals))}]
        [:label "Saturation 0-100"]
        [:input.form-control-range.sat-range.w-100 {
          :type "range" :min "0" :max "100" :value (:s @hslvals)
          :on-change (fn [e] (swap! hslvals assoc :s (.. e -target -value))(convert @hslvals))}]
        [:label "Luminance 0-100"]
        [:input.form-control-range.lum-range.w-100 {
          :type "range" :min "0" :max "100" :value (:l @hslvals)
          :on-change (fn [e] (swap! hslvals assoc :l (.. e -target -value))(convert @hslvals))}]]
      [:div.row-fluid.my-3.p-2 {:style {:color (fontcolour (:hex @hslvals)) :background-color (:hex @hslvals)}}
        [:div (str "Hue: " (:h @hslvals) (gstring/unescapeEntities "&deg;") " Saturation: " (:s @hslvals) "% Luminance: " (:l @hslvals) "%")]
        [:div (str "RGB: " (:rgb @hslvals))]
        [:div (str "Hex: " (:hex @hslvals))]]
      [:div.row [:a {:href "https://en.wikipedia.org/wiki/HSL_and_HSV" :target "_blank"} "https://en.wikipedia.org/wiki/HSL_and_HSV"]]]
   [:div.col-sm-6
    [:div.row-fluid
      [:div.btn-group.d-flex {:role "group"}
        (map (fn [c] 
          ^{:key (gensym)}[:button.btn.btn-outline-dark.w-100 {
            :on-click #(update-rand-hsl c)
            :style {:background-color (rand-hsl-hue c)}}
            c]) ["Yellow" "Green" "Blue" "Purple" "Red" "Any"])]]
    [:div.row
      [:div.col-sm-6.offset-3
        [:div.row-fluid [:div.h4.text-center (:name @samples)]]
        (for [h (:colours @samples)]
          ^{:key (gensym)}[:div.row.py-2 {:style {:background-color h :color (fontcolour h)}} h])
        ]]]])
      
(update-rand-hsl "Any")
(convert @hslvals)
(r/render [Page] (.getElementById js/document "app"))