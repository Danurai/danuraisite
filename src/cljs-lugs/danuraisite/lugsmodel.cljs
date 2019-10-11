(ns danuraisite.lugsmodel)

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