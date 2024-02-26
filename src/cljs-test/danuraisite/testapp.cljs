(ns danuraisite.testapp
  (:require [reagent.core :as r]))

(def appdata (r/atom nil))

(def width 500)
(def height 600)

; Edge of the empire force map

(def talents [
  {:id 0  :name "Brace" :ap "A" :cost 5 :trained false} 
  {:id 1  :name "Toughened" :ap "P" :cost 5 :trained false}
  {:id 2  :name "Intimidating" :ap "A" :cost 5 :trained false}
  {:id 3  :name "Defensive Stance" :ap "A" :cost 5 :trained false}
  {:id 4  :target 5  :name "Spare Clip" :ap "P" :cost 10 :trained false}
  {:id 5  :target 1  :name "Jury Rigged" :ap "P" :cost 10 :trained false}
  {:id 6  :target 5  :name "Point Blank" :ap "P" :cost 10 :trained false}
  {:id 7  :target 3  :name "Disorient" :ap "P" :cost 10 :trained false}
  {:id 8  :target 9  :name "Toughened" :ap "P" :cost 15 :trained false}
  {:id 9  :target 5  :name "Armor Master" :ap "P" :cost 15 :trained false}
  {:id 10 :target 9  :name "Natural Enforcer" :ap "A" :cost 15 :trained false}
  {:id 11 :target 7  :name "Stunning Blow" :ap "A" :cost 15 :trained false}
  {:id 12 :target 13 :name "Jury Rigged" :ap "P" :cost 20 :trained false}
  {:id 13 :target 9  :name "Tinkerer" :ap "P" :cost 20 :trained false}
  {:id 14 :target 13 :name "Deadly Accuracy" :ap "P" :cost 20 :trained false}
  {:id 15 :target 11 :name "Improved Stunning Blow" :ap "A" :cost 20 :trained false}
  {:id 16 :target 17 :name "Intimidating" :ap "A" :cost 25 :trained false}
  {:id 17 :target 13 :name "Dedication" :ap "p" :cost 25 :trained false}
  {:id 18 :target 17 :name "Improved Armor Master" :ap "p" :cost 25 :trained false}
  {:id 19 :target 15 :name "Crippling Blow" :ap "A" :cost 25 :trained false}
  ])

(def nodes (clj->js talents))
(def node-links 
  (clj->js (remove nil? (mapv #(if-let [tgt (:target %)] (hash-map :source (:id %) :target tgt)) talents))))

(.. js/d3
    (select "#graph")
    (selectAll "circle")
    remove)


(def link-elems 
  (.. js/d3
      (select "#graph")
      (selectAll "line")
      (data node-links)
      (join "line")
      (attr "stroke" "black")))
(def node-elems
  (.. js/d3
      (select "#graph")
      (selectAll "circle")
      (data nodes)
      (join "circle")
      (attr "r" 10)))

(defn ticked []
  (.. node-elems
      (attr "cx" (fn [_ idx] (.-x (get nodes idx))))
      (attr "cy" (fn [_ idx] (.-y (get nodes idx))))
      (attr "fill" (fn [_ idx] (if (= "A" (.-ap (get nodes idx))) "maroon" "darkblue"))))
  (.. link-elems
      (attr "x1" (fn [_ idx] (-> (get node-links idx) .-source .-x)))
      (attr "y1" (fn [_ idx] (-> (get node-links idx) .-source .-y)))
      (attr "x2" (fn [_ idx] (-> (get node-links idx) .-target .-x)))
      (attr "y2" (fn [_ idx] (-> (get node-links idx) .-target .-y)))
      
      ))

(let [simulation (.. js/d3
                     (forceSimulation nodes)
                     (force "charge" (.. js/d3 forceManyBody (strength -20)))
                     ;(force "center" (js/d3.forceCenter (/ width 2) (/ height 2)))
                     (force "x" (.. js/d3 forceX (x (fn [d] (+ 100 (* 100 (mod  (.-id d) 4)))))))
                     (force "y" (.. js/d3 forceY (y (fn [d] (+ 100 (* 100 (quot (.-id d) 4)))))))
                     (force "link"   (.. js/d3 forceLink (links node-links)))
                     (on "tick" ticked)
                     
                     )])

;(r/render [app] (.getElementById js/document "app"))

;; NOTE for packaging the d3 functions used 
;; need to be added to js/externs.js 
;; externs need to be added to the cljs prod build, 
;; or optimisation set not= advanced e.g. whitespace