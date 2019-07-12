(ns danuraisite.model
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs_http.client :as http]
    [cljs.core.async :refer [<!]]))
  
(def hints (atom {
  :name "Who are you?"
  :concept "Who are you? What is your involvement in the situation? What is your relationship with the other characters?"
  :badhabits "What habits do you have that's likely to cause you trouble? Gain Survival Points for using."
  :notes ""
  :identify "Crime Scene Investigator, Insatiable Curiosity, Librarian, Discrete, Hide in Plain Sight, Look Over There!"
  :persuade "Charmer, Mr Fix-it, Position of Authority, Intimidating, Saboteur, Thug."
  :pursue   "Headlock, No Escape, Trailblazer, Getaway Driver, Houdini, Run for your Lives!"
  :assault  "Fisticuffs, Gun Totin', Vampire Slayer, Bodyguard, Dodge, Holy Sacraments."}))
      
(def blank-victim {
    :sp 5
    :stats {
      :identify {:name "Identify" :score 5 :complement "Obscure" :specs ()}
      :persuade {:name "Persuade" :score 5 :complement "Dissuade" :specs ()}
      :pursue {:name "Pursue" :score 5 :complement "Escape" :specs ()}
      :assault {:name "Assault" :score 5 :complement "Protect" :specs ()}}})      
      
(def don-data (r/atom blank-victim))
      
(def don-victims (r/atom nil))

(defn add-specialisation! [stat spec-score]
  (let [statkey (-> stat :key)
       specialisation (:specialisation stat)]
    (if (pos? (:score stat)) (swap! don-data update-in [:stats statkey :score] dec))
    (swap! don-data update-in [:stats statkey :specialisations] conj {:name specialisation :score spec-score :id (gensym "sp_")})
    (prn @don-data)))
    
(defn delete-specialisation! [statkey id]
  (swap! don-data assoc-in [:stats statkey :specialisations] 
    (->> @don-data :stats statkey :specialisations (remove #(= (:id %) id)))))
    
(defn adduidtodata [ resp ]
  (-> (.parse js/JSON (:data resp))
      (js->clj :keywordize-keys true)
      (assoc :uid (:uid resp))))
  
(defn getvictims! []
  (go (let [response (<! (http/get "/don/api/victims"))]
        (if (= "false" (:body response))
            (reset! don-victims false)
            (reset! don-victims (map #(adduidtodata %)(:body response)))))))

(defn savevictim []
  (go (let [response (<! (http/post "/don/save" {:form-params {:data (.stringify js/JSON (clj->js @don-data))}}))]
    (prn response)
    (getvictims!)
    )))

(defn choose-victim [ uid ]
  (go (let [response (<! (http/get (str "/don/api/victims/" uid)))]
    (reset! don-data (adduidtodata (:body response))))))
    

(defn reset-don! []
  (getvictims!)
  (reset! don-data blank-victim))
         