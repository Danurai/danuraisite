(ns kasei.hexes
  (:require
    [kasei.model :refer [appstate]]))

(def ^:const sqrt3 (.sqrt js/Math 3))

(defn- pointy_hex_to_pixel [ origin size hex ] 
  (let [q     (first hex)
        r     (second hex)]
    {:x (+ (:x origin) (* size (+ (* sqrt3 q) (* r (/ sqrt3 2))))) 
     :y (+ (:y origin) (* size                (* r (/ 3 2))))}))
     
; Conversion fns
(defn- axial_to_cube [ hex ]
  [(nth hex 0) (reduce - 0 hex) (nth hex 1)])
  
(defn- cube_to_axial [ cube ]
  [(nth cube 0) (nth cube 2)])
  
; In the cube coordinate system, each hexagon is a cube in 3d space. 
; Adjacent hexagons are distance 1 apart in the hex grid but distance 
; 2 apart in the cube grid. This makes distances simple. 
; In a square grid, Manhattan distances are abs(dx) + abs(dy). 
; In a cube grid, Manhattan distances are abs(dx) + abs(dy) + abs(dz). 
; The distance on a hex grid is half that:
(defn- cube_distance [ a b ]
  (/
    (apply + (map #(Math/abs (+ %1 %2)) a b))
    2))
  
(defn hex_distance [ a b ]
  (cube_distance 
    (axial_to_cube a)
    (axial_to_cube b)))
  
  
(defn- cube_round [cube]
  (let [rx (Math/round (nth cube 0))
        ry (Math/round (nth cube 1)) 
        rz (Math/round (nth cube 2))
        dx (-> rx (- (nth cube 0)) Math/abs)
        dy (-> ry (- (nth cube 1)) Math/abs)
        dz (-> rz (- (nth cube 2)) Math/abs)]
    (if (and (> dx dy) (> dx dz))
        [(reduce - [0 ry rz]) ry rz]
        (if (> dy dz)
            [rx (reduce - [0 rx rz]) rz]
            [rx ry (reduce - [0 rx ry])]))))
     
(defn pixel_to_pointy_hex [ origin coords hexsize ]
  (let [px (- (first coords) (:x origin))
        py (- (second coords) (:y origin))
        pq (/ (- (* (/ sqrt3 3) px) (/ py 3)) hexsize)
        pr (/                  (* (/ 2 3) py) hexsize)]
    (-> [pq pr] axial_to_cube cube_round cube_to_axial)))
    
(defn- pointy_hex_corner [ center size i ]
  (let [angle_deg (-> 60 (* i) (- 30))
        angle_rad (-> Math/PI (/ 180) (* angle_deg))]
    [(+ (:x center) (* size (Math/cos angle_rad)))
     (+ (:y center) (* size (Math/sin angle_rad)))]))
     

(defn- citystyle! [ ctx ]
  (set! (.-shadowColor ctx) "white")
  (set! (.-shadowBlur ctx) 15)
  (set! (.-lineWidth ctx)  3)
  (set! (.-strokeStyle ctx) "rgba(255,255,255,0.5)")
  (set! (.-fillStyle ctx) "rgba(255,255,255,0.05)")
  (.clip ctx)
  (.fill ctx)
  (.stroke ctx)
  (.stroke ctx)
  (.stroke ctx))
  
(defn- oceanstyle! [ ctx ]
  (set! (.-shadowColor ctx) "blue")
  (set! (.-shadowBlur ctx) 15)
  (set! (.-lineWidth ctx)  3)
  (set! (.-strokeStyle ctx) "rgba(0,0,255,0.5)")
  (set! (.-fillStyle ctx) "rgba(0,0,255,0.05)")
  (.clip ctx)
  (.fill ctx)
  (.stroke ctx)
  (.stroke ctx)
  (.stroke ctx))
  
     
(defn draw_pointy_hex [ ctx origin size hex & args]
  (let [hexcenter (pointy_hex_to_pixel origin size hex)]
    (.beginPath ctx)
    (let [[x y] (pointy_hex_corner hexcenter (- size 2) 0)]
      (.moveTo ctx x y))
    (doseq [[x y] (map #(pointy_hex_corner hexcenter (- size 2) %) (range 1 6))]
      (.lineTo ctx x y))
    (.closePath ctx)
    
    ; Restrictions
    (.save ctx)
    (case (-> @appstate :board :restricted (get hex))
      :ocean (oceanstyle! ctx)
      :city  (citystyle! ctx)
      nil)
    (.restore ctx)
    ;(case (first args)
    ;  "greenery" (set! (.-fillStyle ctx) "rgb(0,120,0)")
    ;  nil)
    ;(.fill ctx)
    (let [rgb (if (= hex (:mouseqr @appstate))
                 "rgba(0,255,0,1)" 
                 "rgba(255,255,255,0.6)")]
      (set! (.-strokeStyle ctx) rgb))
    (.stroke ctx)
    ))