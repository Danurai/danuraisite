(ns danuraisite.lugsmodel
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
(def party (r/atom nil))
(def apidata (r/atom {}))
(def savedparties (r/atom {}))

    
(def ^:const names ["Lagather" "Bjorn" "Helmdale" "Rhianna" "Musgrove" "Toad" "Felps" "Germintrude"])
(def ^:const limits [
  ["Weapons, Outfits and Kit: 1 Basic Weapon, Talents: 3"]
  ["Weapons, Outfits and Kit: 2, Talents: 3"]])
  
(def fa-icons (r/atom nil))


(defn- convert [fa-icons txt]
  (if-let [symbol (re-matches #"\[(\w+)\]" txt)]
    ^{:key (gensym)}[:i {:class (get fa-icons (second symbol)) :title (second symbol)}]
    txt))

(defn- makespan [res] 
  (apply conj [:span] 
    (reduce 
      #(if (string? %2) 
        (if (string? (last %1)) 
          (conj (-> %1 drop-last vec) (str (last %1) %2)) 
          (conj (vec %1) %2)) 
        (conj (vec %1) %2)) [""] res)))

(defn markdown [ txt fa-icons ]
  (->> txt
      (re-seq #"\[\w+\]|\n|." )
      (map #(convert fa-icons %))
      makespan))
      

(defn party-with-cards []
  (assoc @party :heros
    (reduce merge (map 
      (fn [[id h]]
        (hash-map id 
                 (assoc h :cards (->> @apidata :cards (filter #(= (:hero %) id)) (map :id))))
      ) (:heros @party )))))
      
(defn updatecardsbyselection [ ps crd ]
  (map (fn [c]
    (if (= (:type crd) "Occupation")
        (if (= (:hero c) (:hero @ps))
          (dissoc c :hero)
          (if (= (:id c) (:id crd))
              (assoc c :hero (:hero @ps))
              c))
      (if (= (:id c) (:id crd))
          (if (= (:hero c) (:hero @ps))
              (dissoc c :hero)
              (assoc c :hero (:hero @ps)))
          c))
    ) (:cards @apidata)))
      
(defn selectcard! [ ps crd ]
  (swap! apidata assoc :cards (updatecardsbyselection ps crd)))

(defn add-hero! []
  (swap! party assoc-in [:heros (keyword (gensym "hero"))] {:name (rand-nth names) :lvl 1}))
  
(defn delete-hero! [ e ps id h ]
  (.stopPropagation e)
  (swap! ps assoc :confirm 
    (hash-map :msg (str "Are you sure you want to remove " (:name h))
             :fn (fn []
              (if (= id (:hero @ps))
                (swap! ps dissoc :hero)) 
              (swap! party update :heros dissoc id)
              (swap! apidata assoc :cards (map #(if (= (:hero %) id) (dissoc % :hero) %) (:cards @apidata)))))))
        
(defn- resetapidata! []
  (go (let [response (<! (http/get "/api/data/lu"))]
    (reset! apidata (:body response))
    (reset! fa-icons (->> response :body :icons (map #(hash-map (:name %) (:fa %))) (apply merge))))))
    
(defn- resetparties! []
  (go (let [response (<! (http/get "/lu/api/parties"))]
    (if (= "false" (:body response))
        (reset! savedparties false)
        (reset! savedparties (map #(assoc % :data (js->clj (.parse js/JSON (:data %)) :keywordize-keys true)) (:body response)))))))
          
(defn init! []
  (resetapidata!)
  (resetparties!))
  
(defn resetpage! [ ps ]
  (reset! ps {:cardlist "Occupation"})
  (reset! party nil)
  (init!))
    
(defn saveparty![ party ]
  (go (let [response (<! (http/post "/lu/party/save" {:form-params (assoc @party :data (.stringify js/JSON (clj->js (:heros (party-with-cards)))))}))]
    (resetparties!))))
        
(defn deleteparty! [ party ]
  (go (let [response (<! (http/post "/lu/party/delete" {:form-params {:uid (:uid party)}}))]
        (init!))))
  
(defn- newparty! []
  (reset! party {})
  (add-hero!))
  
(defn- loadparty! [ p ]
  (reset! party (hash-map 
    :uid (:uid p) 
    :name (:name p) 
    :heros (->> p :data (map (fn [[id h]] (hash-map id (dissoc h :cards)))) (reduce merge))))
  (swap! apidata assoc :cards
    (reduce (fn [cards [k h]]
      (map
        #(if (contains? (-> h :cards set) (:id %)) (assoc % :hero k) %)
      cards)) (map #(dissoc % :hero) (:cards @apidata)) (-> p :data))))

(defn setparty! [ ps p ]
  (if (nil? p) (newparty!) (loadparty! p))
  (swap! ps assoc 
    :screen :edit
    :lastsave (party-with-cards)))