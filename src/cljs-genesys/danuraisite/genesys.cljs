(ns danuraisite.genesys
  (:require 
    [reagent.core :as r]
    [danuraisite.genesys-data :refer [species stats skills careers]]))

(def data (r/atom nil))

(defn save! [] (.setItem js/localStorage "genesys" (pr-str @data)))
(defn load! [] (if-let [appdata (.getItem js/localStorage "genesys")] (reset! data (cljs.reader/read-string appdata))))
  
(defn init-char
  ([ s c x ]
    (let [spec-data (->> species (filter #(= (:name %) s)) first)
          career    (->> careers (filter #(= (:name %) c)) first)
          spex      (->> career :spex (filter #(= (:name %) x)) first)
          c-skills  (-> career :skills set)
          x-skills  (-> spex :skills set) ]
      (reset! data 
        (hash-map 
          :name ""
          :species spec-data
          :career  career
          :spex    spex
          :xp (:basexp spec-data)
          :xpspent 0
          :health (:health spec-data)
          :skills (map #(if (contains? c-skills (:name %))
                        (assoc % :career "C") 
                        (if (contains? x-skills (:name %)) 
                            (assoc % :career "X") %)) skills)
          :free_skills (:free_skills spec-data)
          :stats (map #(let [rank (get (:stats spec-data) (:idx %) 0)] (-> % (assoc :rank rank :base rank))) stats)
          :talents ""
      ))))
  ([ s c ] (init-char s c (->> careers (filter #(= (:name %) c)) (map :name) first)))
  ([ s ] (init-char s (-> @data :career :name) (-> @data :spex :name))))

(defn init []
  ;(if (nil? (load!)) 
      (let [s (rand-nth species) c (rand-nth careers) x (rand-nth (:spex c))] 
        (init-char 
          (:name s)
          (:name c) 
          (:name x))))

(defn clear! [] (.removeItem js/localStorage "genesys") (init))

(defn calc-xp [  ]
  (+ 
    (apply +
      (map 
        (fn [skill]
          (apply + (for [n (range (:rank skill) (if (:free_rank skill) 1 0) -1)] (+ (if (:career skill) 0 5) (* n 5))))
        ) (:skills @data)))
    (apply +
      (map 
        (fn [stat]
          (apply + (for [n (range (:rank stat) (:base stat) -1)] (* n 10)))
        ) (:stats @data)))
    (apply +
      (mapv #(-> % last js/parseInt) (re-seq #"\((\d+)\)" (:talents @data))))
  ))

(defn update-stat [ key val ]
  (swap! data assoc 
    :stats  (map #(if (= (:abbr %) key)
                      (assoc % :rank (js/parseInt val))
                      %) (:stats @data))
    :health (map #(if (= (:stat %) key)
                      (assoc % :rank (+ (js/parseInt val) (:base %) (:mod %)))
                      %) (:health @data)))
  (swap! data assoc :xpspent (calc-xp)))

(defn update-health [ health val ]
  (let [stat (->> @data :stats (filter #(= (:abbr %) (:stat health))) first :rank)]
    (swap! data assoc 
      :health (map #(if (= (:name %) (:name health))
                        (assoc % :mod (js/parseInt val) :rank (+ stat (:base %) (js/parseInt val)))
                        %) (:health @data)))))

(defn update-career-skill [ skill type ]
  (swap! data assoc :skills
    (map #(if (= (:name %) skill)
              (if type 
                  (assoc % :career type)
                  (dissoc % :career))
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

(defn update-talents [ talents] 
  (swap! data assoc :talents talents)
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
      [:div.me-2 {:title "Specialisation"} "X."]
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
        [:span.me-2 {:style {:font-weight (if (:career skill) "bold" "")}} (str (:name skill) " (" (:stat skill) ")")]
        [:input.form-check-input.ms-auto.me-2 {:type "checkbox" :title "Career Skill" :on-change #(update-career-skill (:name skill) (if (.. % -target -checked) "C" nil)) :checked (= (:career skill) "C")}]
        [:input.form-check-input.me-2 {:type "checkbox" :title "Specialisation Skill" :on-change #(update-career-skill (:name skill) (if (.. % -target -checked) "X" nil)) :checked (= (:career skill) "X")}]
        [:input.form-check-input.me-2 {:type "checkbox" :title "Free Rank" :on-change #(update-free-skill (:name skill) (.. % -target -checked)) :checked (:free_rank skill)}]
        [:input.slider {:type "range" :min 1 :max 5 :value rank :read-only true :class (if rankdif (str "slider-m" rankdif) "") 
          :on-click  #(let [rect (-> % .-target .getBoundingClientRect)
                            left (- (.-clientX %)  (.-left rect))
                            val (->> 5 (/ (.-width rect)) (quot left) inc)]
                          (update-skill (:name skill) val))}]])])

(defn App [ req ]
  [:div.container.my-3
    [:div.mb-2
      [:div
        [:button.btn.btn-primary.me-3 {:on-click #(save!)} "Save"]
        [:button.btn.btn-warning.me-3 {:on-click #(load!)} "Load"]
        [:button.btn.btn-danger       {:on-click #(clear!)} "Reset"]]]
    [:div.d-flex.mb-1
      [:h5.my-auto.me-2 "Name"]
      [:input.form-control {:value (:name @data) :on-change #(swap! data assoc :name (.. % -target -value))}]
    ]
    [:div.d-flex.mb-1
      [:b.my-auto.me-2 "Species"]
      [:select.form-control {:value (-> @data :species :name) :on-change #(init-char (.. % -target -value))}
        (doall (for [s species]
          [:option {:key (gensym)} (:name s)]))]
      [:b.my-auto.mx-2 "Career"]
      [:select.form-control {:value (-> @data :career :name) :on-change #(init-char (-> @data :species :name) (.. % -target -value))}
        (doall (for [s careers]
          [:option {:key (gensym)} (:name s)]))]
      [:b.my-auto.mx-2 "Spec."]
      [:select.form-control {:value (-> @data :spex :name) :on-change #(init-char (-> @data :species :name) (-> @data :career :name) (.. % -target -value))}
        (doall (for [s (-> @data :career :spex)]
          [:option {:key (gensym)} (:name s)]))]]
    [:div.d-flex.mb-1
      [:b.my-auto.me-2 "Talents"]
      [:input.form-control {:value (:talents @data) :on-change #(update-talents (.. % -target -value))}]]
    [:div.d-flex.mb-1
      [:b.me-3 (str "XP: " (:xpspent @data) "/" (:xp @data)) ]
      [:span.me-3 (str (->> @data :skills (filter :career) count) " Career Skills")]
      [:span.me-3 (str (->> @data :skills (filter :free_rank) count) " Free Ranks")]]
    
    [:div.d-flex.my-1.justify-content-around
      (for [h (:health @data)]
        [:div.mx-2 {:key (gensym)} 
          [:div  {:style {:position "relative"}}
            [:div.text-center.statlabel {:style {:border-color "darkblue" :background "darkblue" :color "white"}} (:name h)]]
          [:h4.text-center (:rank h)]
          [:small.d-flex.justify-content-between
            [:span "-10"]
            [:span (str "mod:" (:mod h))]
            [:span "+10"]]
          [:input {:type "range" :min -10 :max 10 :value (:mod h) :on-change #(update-health h (.. % -target -value))}]])]
    
    [:div.d-flex.my-1.justify-content-between
      (for [s (:stats @data)]
        [:div {:key (gensym)}
          [:div  {:style {:position "relative"}} [:div.statbox (:rank s)]]
          [:div  {:style {:position "relative"}}  
            [:div.text-center.statlabel {:style {:border-color "maroon" :background-color "maroon" :color "white"}} (:name s)]]
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