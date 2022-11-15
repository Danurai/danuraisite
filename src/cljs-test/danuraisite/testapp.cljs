(ns danuraisite.testapp
  (:require [reagent.core :as r]))

(def data (r/atom nil))

(def stats [
	{:idx 0 :name "Brawn" :abbr "Br" :rank 0 :base 0}
	{:idx 1 :name "Agility" :abbr "Ag" :rank 0 :base 0}
	{:idx 2 :name "Intellect" :abbr "Int" :rank 0 :base 0}
	{:idx 3 :name "Cunning" :abbr "Cun" :rank 0 :base 0}
	{:idx 4 :name "Willpower" :abbr "Will" :rank 0 :base 0}
	{:idx 5 :name "Presence" :abbr "Pr" :rank 0 :base 0}])
(def species  [
	{:name "Droid"   :stats [1 1 1 1 1 1] :basexp 175 :health [{:name "Soak" :base 0 :stat "Br" :rank 1} {:name "Wounds" :base 10 :stat "Br" :rank 11} {:name "Strain" :base 10 :stat "Will" :rank 11}] :free_skills []}
	{:name "Human"   :stats [2 2 2 2 2 2] :basexp 110 :health [{:name "Soak" :base 0 :stat "Br" :rank 2} {:name "Wounds" :base 10 :stat "Br" :rank 12} {:name "Strain" :base 10 :stat "Will" :rank 12}] :free_skills ["any" "any"]}
	{:name "Twi'Lek" :stats [1 2 2 2 2 3] :basexp 100 :health [{:name "Soak" :base 0 :stat "Br" :rank 1} {:name "Wounds" :base 10 :stat "Br" :rank 11} {:name "Strain" :base 11 :stat "Will" :rank 13}] :free_skills ["Charm|Deception"]}])
(def skills [
	{:name "Astrogation" :stat "Int" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Athletics" :stat "Br" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Charm" :stat "Pr" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Coercion" :stat "Will" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Computers" :stat "Int" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Cool" :stat "Pr" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Coordination" :stat "Ag" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Deception" :stat "Cun" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Discipline" :stat "Will" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Leadership" :stat "Pr" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Mechanics" :stat "Int" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Medicine" :stat "Int" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Negotiation" :stat "Pr" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Perception" :stat "Cun" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Piloting - Planetary" :stat "Ag" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Piloting - Space" :stat "Ag" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Resilience" :stat "Br" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Skullduggery" :stat "Cun" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Stealth" :stat "Ag" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Streetwise" :stat "Cun" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Survival" :stat "Cun" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Vigilance" :stat "Will" :career "" :free_rank false :rank 0 :type "General"}
	{:name "Brawl" :stat "Br" :career "" :free_rank false :rank 0 :type "Combat"}
	{:name "Gunnery" :stat "Ag" :career "" :free_rank false :rank 0 :type "Combat"}
	{:name "Melee" :stat "Br" :career "" :free_rank false :rank 0 :type "Combat"}
	{:name "Ranged - Light" :stat "Ag" :career "" :free_rank false :rank 0 :type "Combat"}
	{:name "Ranged - Heavy" :stat "Ag" :career "" :free_rank false :rank 0 :type "Combat"}
	{:name "Core Worlds" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Education" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Lore" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Outer Rim" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Underworld" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Xenology" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Other" :stat "Int" :career "" :free_rank false :rank 0 :type "Knowledge"}])
(def careers [
  {
    :name "Bounty Hunter" 
    :skills ["Athletics" "Perception" "Piloting - Planetary" "Piloting - Space" "Skullduggery" "Vigilance" "Brawl" "Ranged - Heavy"]
    :spex [
      {:name "Gadgeteer" :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"]}
      {:name "Assassin" :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"]}
      {:name "Survivalist" :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"]}]
  }])


(defn save! []  (.setItem js/localStorage "genesys" (pr-str @data)))
(defn load! [] (reset! data (cljs.reader/read-string (.getItem js/localStorage "genesys"))))
  
(defn init-char 
  ([ s c x ]
    (let [spec-data (->> species (filter #(= (:name %) s)) first)
          career    (->> careers  (filter #(= (:name %) c)) first)
          spex      (->> career :spex (filter #(= (:name %) x)) first)
          c-skills  (-> (:skills career) (concat (:skills spex)) set)  ]
      (prn s c x)
      (reset! data 
        (hash-map 
          :species spec-data
          :career  career
          :spex    spex
          :xp (:basexp spec-data)
          :xpspent 0
          :health (:health spec-data)
          :skills (map #(if (contains? c-skills (:name %)) (assoc % :career "Y") %) skills)
          :free_skills (:free_skills spec-data)
          :stats (map #(let [rank (get (:stats spec-data) (:idx %) 0)] (-> % (assoc :rank rank :base rank))) stats)
      ))))
  ([ s c ] (init-char s c (->> careers (filter #(= (:name %) c)) (map :name) first)))
  ([ s ] (init-char s (-> @data :career :name) (-> @data :spex :name))))

(defn init [] 
  (if (nil? (load!)) 
      (let [c (rand-nth careers)
            x (rand-nth (:spex c))] 
        (init-char 
          (rand-nth (map :name species)) 
          (:name c) 
          (:name x)))))

(defn reset-char! [] (.removeItem js/localStorage "genesys") (init-char "Human" ))

(defn calc-xp [  ]
  (+ 
    (apply +
      (map 
        (fn [skill]
          (apply + (for [n (range (:rank skill) (if (:free_rank skill) 1 0) -1)] (+ (if (= (:career skill) "Y") 0 5) (* n 5))))
        ) (:skills @data)))
    (apply +
      (map 
        (fn [stat]
          (apply + (for [n (range (:rank stat) (:base stat) -1)] (* n 10)))
        ) (:stats @data)))
  ))

(defn update-stat [ key val ]
  (swap! data assoc 
    :stats  (map #(if (= (:abbr %) key)
                      (assoc % :rank (js/parseInt val))
                      %) (:stats @data))
    :health (map #(if (= (:stat %) key)
                      (assoc % :rank (+ (js/parseInt val) (:base %)))
                      %) (:health @data)))
  (swap! data assoc :xpspent (calc-xp)))

(defn update-career-skill [ skill bool ]
  (swap! data assoc :skills
    (map #(if (= (:name %) skill)
              (assoc % :career (if bool "Y" ""))
              %) (:skills @data)))
  (swap! data assoc :xpspent (calc-xp)))

(defn update-free-skill [ skill bool ]
  (swap! data assoc :skills
    (map #(if (= (:name %) skill)
              (-> % 
                  (assoc  :free_rank bool)
                  (update :rank (if bool inc dec)))
              %) (:skills @data)))
  (swap! data assoc :xpspent (calc-xp)))

(defn update-skill [ key val ]
  (swap! data assoc :skills
    (map 
      (fn [s]
        (let [rank (:rank s)] ;(if (:free_rank s) 1 0)
          (if (= (:name s) key)
              (assoc s :rank (if (= rank val) (dec val) val))
              s)))
      (:skills @data)))
  (swap! data assoc :xpspent (calc-xp)))

(defn skill-block [ skills type stats ]
  [:div.my-3
    [:div.d-flex 
      [:h5 (str type " Skills")]
      [:div.me-2.ms-auto {:title "Career"} "C."]
      [:div {:title "Trained"} "T."]
      [:div {:style {:width "110px"}}]
    ]
    (for [skill (filter #(= (:type %) type) skills)
          :let [statrank  (->> stats (filter #(= (:abbr %) (:stat skill))) first :rank)
                skillrank (:rank skill)
                rankdif   (if (> skillrank 0) (-> statrank (- skillrank) Math/abs))
                rank      (Math/max statrank skillrank)
                ]]
      [:div.d-flex {:key (gensym) }
        [:span.me-2 {:style {:font-weight (if (= (:career skill) "Y") "bold" "")}} (str (:name skill) " (" (:stat skill) ")")]
        [:input.form-check-input.ms-auto.me-2 {:type "checkbox" :on-change #(update-career-skill (:name skill) (.. % -target -checked)) :checked (= (:career skill) "Y")}]
        [:input.form-check-input.me-2 {:type "checkbox" :on-change #(update-free-skill (:name skill) (.. % -target -checked)) :checked (:free_rank skill)}]
        [:input.slider {:type "range" :min 1 :max 5 :value rank :class (if rankdif (str "slider-m" rankdif) "") 
          :on-click  #(let [rect (-> % .-target .getBoundingClientRect)
                            left (- (.-clientX %)  (.-left rect))
                            val (->> 5 (/ (.-width rect)) (quot left) inc)]
                          (update-skill (:name skill) val))}]
        ]
      )])

(defn App [ req ]
  [:div.container.my-3
    [:div.mb-2
      [:div
        [:button.btn.btn-primary.me-3 {:on-click #(save!)} "save"]
        [:button.btn.btn-warning {:on-click #(load!)} "load"]]]
    [:div.d-flex.mb-1
      [:h5.my-auto.me-2 "Name"]
      [:input.form-control]
    ]
    [:div.d-flex.mb-1
      [:b.my-auto.me-2 "Species"]
      [:select.form-control {:defaultValue (-> @data :species :name) :value (-> @data :species :name) :on-change #(init-char (.. % -target -value))}
        (doall (for [s species]
          [:option {:key (gensym)} (:name s)]))]
      [:b.my-auto.mx-2 "Career"]
      [:select.form-control {:defaultValue (-> @data :career :name) :value (-> @data :career :name) :on-change #(init-char (-> @data :species name) (.. % -target -value))}
        (doall (for [s careers]
          [:option {:key (gensym)} (:name s)]))]
      [:b.my-auto.mx-2 "Spec."]
      [:select.form-control {:defaultValue (-> @data :spex :name) :value (-> @data :spex :name) :on-change #(init-char (-> @data :species name) (-> @data :career :name) (.. % -target -value))}
        (doall (for [s (-> @data :career :spex)]
          [:option {:key (gensym)} (:name s)]))]]
    [:div.d-flex
      [:b.me-3 (str "XP: " (:xpspent @data) "/" (:xp @data)) ]
      [:span.me-3 (str (->> @data :skills (filter #(= (:career %) "Y")) count) " Career Skills")]
      [:span.me-3 (str (->> @data :skills (filter :free_rank) count) " Free Ranks")]
    ]
    [:div.d-flex.my-1.justify-content-around
      (for [h (:health @data)]
        [:div.mx-2 {:key (gensym) :style {:position "relative"}} 
          [:div.text-center.statlabel {:style {:background "darkblue" :min-width "100px" :color "white"}} (:name h)]
          [:h4.text-center (:rank h)]])]
    [:div.d-flex.my-1.justify-content-between
      (for [s (:stats @data)]
        [:div {:key (gensym)}
          [:h2.text-center (:rank s)]
          [:div.text-center {:style {:background-color "maroon" :color "white"}} (:name s)]
          [:input {:type "range" :min (:base s) :max 5 :value (:rank s) :on-change #(update-stat (:abbr s) (.. % -target -value))}]
          ]
        )]
    [:div.row
      [:div.col-6 (skill-block (:skills @data) "General" (:stats @data))]
      [:div.col-6
        (skill-block (:skills @data) "Combat"  (:stats @data))
        (skill-block (:skills @data) "Knowledge"  (:stats @data))
      ]
      ]
    ])

(init)
(r/render [App] (.getElementById js/document "app"))