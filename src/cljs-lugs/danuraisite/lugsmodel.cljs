(ns danuraisite.lugsmodel
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
(def party (r/atom nil))
(def apidata (r/atom {}))
(def savedparties (r/atom {}))

    
(def names ["Lagather" "Bjorn" "Helmdale" "Rhianna" "Musgrove" "Toad" "Felps" "Germintrude"])
(def levels [
  {:upgrade 0 :talent 0}
  {:upgrade 1 :talent 3}
  {:upgrade 2 :talent 3}
  {:upgrade 2 :talent 4}
  {:upgrade 3 :talent 4}
  {:upgrade 4 :talent 4}
  {:upgrade 4 :talent 5}
  {:upgrade 5 :talent 5}
  {:upgrade 6 :talent 5}
])
(def sets [
  {:id "WC" :name "Weeping Caves"}
  {:id "GS" :name "Great Sewer"}
  {:id "AL" :name "Alchemist"}
  {:id "DR" :name "Druid"}
])

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


(defn get-occupation [ hero ]
  (if-not (nil? hero)
    (->> @apidata
         :cards 
         (filter #(= (:type %) "Occupation"))
         (filter #(= (:hero %) hero)))))
         
(defn hero-upgrade-talent-count [ id ]
  (let [n (->> @apidata :cards (filter #(= (:hero %) id)) (map :type) frequencies)]
    (hash-map 
      :upgrade (apply + (map n ["Weapon" "Outfit" "Kit" "Companion"]))
      :talent (apply + (map n ["Skill Talent" "Weapon Talent"])))))
    
      
(defn- updatecardsbyselection [ hero crd ]
  (map 
    #(if (= (:id %) (:id crd))
         (if (= (:hero %) hero)
             (dissoc % :hero)
             (assoc % :hero hero))
         (if (and (= (:type crd) "Occupation") (= (:type %) "Occupation") (= (:hero %) hero))
             (dissoc % :hero)
             %)) (:cards @apidata)))
    
(defn upgrade-toggle! [ crd ]
  (swap! apidata assoc :cards 
    (map 
      #(if (= (:id %) (:id crd))
           (if (:upgrade? %)
               (dissoc crd :upgrade?)
               (assoc crd :upgrade? true))
            %) (:cards @apidata))))
            
(defn hero-upgrade! [ [k v] crd ]
  (if (contains? (-> @party :heros k :upgrades) (:id crd))
      (swap! party update-in [:heros k :upgrades] disj (:id crd))
      (swap! party update-in [:heros k :upgrades] conj (:id crd))))
      
(defn selectcard! [ hero crd ]
  (swap! apidata assoc :cards (updatecardsbyselection hero crd)))


(defn add-hero! []
  (swap! party assoc-in [:heros (keyword (gensym "hero"))] {:name (rand-nth names) :lvl 1 :upgrades #{}}))
  
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
  
(defn resetparty! []
  (reset! party nil)
  (init!))
    
(defn saveparty![ party ]
  (go (let [response (<! (http/post "/lu/party/save" {:form-params (assoc @party :data (.stringify js/JSON (clj->js (:heros (party-with-cards)))))}))]
    (resetparties!)
    )))
        
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
    :heros (reduce-kv #(assoc %1 %2 (-> %3 (dissoc :cards) (assoc :upgrades (-> %3 :upgrades set)))) {} (:data p))))
  (swap! apidata assoc :cards
    (reduce 
      (fn [cards [k h]]
        (map #(if (contains? (-> h :cards set) (:id %)) (assoc % :hero k) %) cards))
      (map #(dissoc % :hero) (:cards @apidata)) 
      (-> p :data))))

(defn setparty! [ ps p ]
  (if (nil? p) (newparty!) (loadparty! p))
  (swap! ps assoc :lastsave (party-with-cards)))
  
(init!)