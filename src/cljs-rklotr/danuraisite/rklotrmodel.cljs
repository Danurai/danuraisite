(ns danuraisite.rklotrmodel
  (:require-macros
    [danuraisite.rklotrmacros :refer [req effect]])
  (:require
    [reagent.core :as r]
    [danuraisite.rklotrdata :as data]
    [danuraisite.rklotrmarkdown :refer [md]]))

(def phases [
  {:id 0 :name "Setup"     :type :setup :steps [:difficulty :players]}
  {:id 1 :name "Bag End"   :type :setup :steps [:gandalf :preparations :nazgulappears]}
  {:id 2 :name "Rivendell" :type :setup :steps [:elrond :council :fellowship]}
  {:id 3 :name "Moria"     :type :board}
])

(def app-state (r/atom nil))

; app-state {
;  :sauron 15, 
;  :phase {
;    :id 1, 
;    :name "Bag End", 
;    :type :setup, 
;    :steps [:gandalf :preparations :nazgulappears], 
;    :step 1, 
;    :stepsdone #{:gandalf}}, 
;   :activestep {
;    :title "Preparations", 
;    :desc "Ring Bearer may [roll] and reveal 4 Hobbit cards face-up for distribution.", 
;    :type :select, 
;    :options [{:title "Roll", :target :ringbearer, :optfn #object[Function]} {:title "Pass", :optfn #object[Function]}]}}
;  :eventqueue
;  :players (
;    {:corruption 0, :a-fn #{}, :ability "", :name "Frodo", :clr "gold", :plyr 1, :ringbearer true, :perm "", :p-fn #{}}
;    {:plyr 2, :name "Sam", :clr "red", :corruption 0, :perm "", :ability "", :p-fn #{}, :a-fn #{}}),

(defn draw-card!
  ([ state plyr ]
    (let [inplay (take-while #(not= (:loc %) :deck) (:cards @state))
          drawdeck (nthrest (:cards @state) (count inplay)) 
          newdeck (apply conj 
                    inplay 
                    (-> drawdeck first (assoc :loc (:plyr plyr))) 
                    (rest drawdeck))]
      (swap! state assoc :cards newdeck)))
  ([ state plyr n ]
    (dotimes [x n] (draw-card! state plyr))))
    
    
(defn- add-event [evt]
  (if (nil? (:eventq @app-state))
      (swap! app-state assoc :eventq [evt])
      (swap! app-state update :eventq conj evt)))
(defn- insert-event [evt]
  (if (nil? (:eventq @app-state))
      (swap! app-state assoc :eventq [evt])
      (swap! app-state assoc :eventq (apply conj [evt] (:eventq @app-state)))))
  
;(defn advance-sauron! [] (swap! app-state update :sauron dec))
(defn set-players [ state value ] (swap! state assoc :players (take value data/hobbits)))
(defn move-sauron [ state value ] (swap! state update :sauron - value))
(defn move-hobbit [ state target value ] 
  (prn target)
  (swap! state assoc :players
    (map #(if (= (:plyr %) (:plyr target))
              (update % :corruption + value)
              %) (:players @state))))
            
(defn no-effect [ state ] nil)

(defn roll [ state target ] (add-event {:event :roll :target target}))
(defn distribute [ state deck count ] (add-event {:event :distribute :deck deck :count count}))
    
(def die [{
    :effect nil
    :msg "Nothing Happens"
  } {
   :effect (effect (move-sauron 1))
   :msg "Sauron moves one step"
  } {
    :effect (effect (move-hobbit target 1))
    :msg "Move your Hobbit one step into the darkness"
  } {
    :effect (effect (move-hobbit target 1))
    :msg "Move your Hobbit one step into the darkness"
  } {
    :effect (effect (move-hobbit target 1))
    :msg "Move your Hobbit one step into the darkness"
  }])
  ;{:effect nil :msg "Discard 2 cards"}
  
      
(def phase-steps {
  :difficulty {
    :title "Difficulty"
    :desc "Select Difficulty - The number of steps Sauron starts towards the Fellowship"
    :type :number
    :min 0
    :max 3
    :val 0
    :effect (effect (move-sauron value))}
  :players {
    :title "Fellowship"
    :desc "Select number of players"
    :type :number
    :min 2
    :max 5
    :val 2
    :effect (effect (set-players value))}
  :gandalf {
    :title "Gandalf"
    :desc "Each player receives 6 hobbit cards."
    :type :select
    :target (req players)
    :options [{
      :title "Draw"
      :effect (effect (draw-card! target 6))
    } {
      :title "Pass"
    }]}
  :preparations {
    :title "Preparations"
    :desc "Ring Bearer may [roll] and reveal 4 Hobbit cards face-up for distribution."
    :type :select
    :target (req (filter :ringbearer players))
    :options [{
      :title "Roll"
      :effect (effect (roll target) (distribute :hobbit 4))
    } {
      :title "Pass"
    }]}
  :nazgulappears {
    :title "Nazgul appears"
    :desc "One player discard 2 Travel or advance Sauron"
    :type :select
    :options [{
        :title "Discard"
        :optfn #()
      } {
        :title "Advance Sauron"
        :effect (effect (move-sauron 1))
      }]}
  :elrond {
    :title "Elrond"
    :desc "Receive Feature Cards"
    :type :select
  }
  :council {
    :title "Council"
    :desc "__EACH PLAYER:__ Pass 1 card face down to the left"
    :type :select
    :target (req (players))
    :effect nil
  }
  :fellowship {
    :title "Fellowship"
    :desc "__EACH PLAYER:__ Discard [fellowship] otherwise [roll]"
    :type :select
    :target (req (players))
    :options [{
      :title "Discard"
      :effect nil
    } {
      :title "Roll"
      :effect (effect (roll target))
    }]
  }
})


(defn set-active-step! [ stepno ]
  (let [as (-> @app-state :phase (get-in [:steps stepno]) phase-steps )]
    (swap! app-state assoc-in [:phase :step] stepno)
    (swap! app-state assoc-in [:phase :stepsdone] (set (take stepno (-> @app-state :phase :steps))))
    (if (:target as) (prn ((:target as) app-state)))
    (swap! app-state assoc :activestep 
      (if (:target as) 
          (assoc as :target ((:target as) app-state))
          as))))
          
(defn next-phase! [ phase ]
  (when-let [next-phase (->> phases (filter #(= (:id %) (-> phase :id inc))) first)]
    (swap! app-state assoc :phase (assoc next-phase :step 0))
    (set-active-step! 0)))
  
(defn prev-phase! [ phase ]
  (when-let [prev-phase (->> phases (filter #(= (:id %) (-> phase :id dec))) first)]
    (swap! app-state assoc :phase (assoc prev-phase :step 0))
    (set-active-step! 0)))
    
(defn next-step! []
  (if (empty? (:eventq @app-state))
      (if (empty? (-> @app-state :activestep :target rest))
          (let [newstepno (-> @app-state :phase :step inc)]
            (if (= newstepno (-> @app-state :phase :steps count))
                (next-phase! (:phase @app-state))
                (set-active-step! newstepno)))
          (swap! app-state assoc-in [:activestep :target] (-> @app-state :activestep :target rest)))))
          
(defn next-event! [] (swap! app-state assoc :eventq (-> @app-state :eventq rest)))
  
    

(defn rolldie! []
  (let [evt (-> @app-state :eventq first)]
    (if (and (nil? (:result evt)) (= :roll (:event evt)))
      (swap! app-state assoc-in [:eventq 0 :result] (rand-nth die)))))
(defn applydieresult! [ event ]
  (let [evt (-> @app-state :eventq first)]
    (when (= :roll (:event evt))
      (next-event!)
      (if-let [res-effect (-> event :result :effect)]
        (res-effect app-state nil nil [(:target event)])))))

(defn init! [] 
  (reset! app-state (hash-map 
    :cards (shuffle data/hobbit-cards) 
    :phase (assoc (get phases 0) :step 0)
    :activestep (-> phases (get-in [0 :steps 0]) phase-steps )
    :sauron 15
    ;:players (take 2 data/hobbits)
    )))
    
    