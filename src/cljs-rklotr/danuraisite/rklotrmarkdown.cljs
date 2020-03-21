(ns danuraisite.rklotrmarkdown)

; Patterns
; _word_ *word* <em>word</en>
; __word__ **word** <b>word</en>



(def markdown_pattern #"([_*]{2}.+?[_*]{1,2})|(\[.+?\])|([a-zA-Z0-9\s\\\!\"\£\$\%\^\&\(\)\-\+\=\{\}\:\;\,\.\@\<\>\?\/]+)")

(def mdicons {
  "roll"      [:span [:i.fas.fa-square]]
  "fellowship" [:i.fas.fa-hands-helping {:title "Fellowship"}]
  "fight"      [:i.ra.ra-crossed-axes {:title "Fight"}]
  "hide"       [:i.fas.fa-tree {:title "Hide"}]
  "travel"     [:i.fas.fa-shoe-prints {:title "Travel"}]
  "wild"       [:i.far.fa-star {:title "Wild Card"}]
})

(defn- makespan [res] 
  (apply conj [:span] 
    (reduce 
      #(if (string? %2) 
        (if (string? (last %1)) 
          (conj (-> %1 drop-last vec) (str (last %1) %2)) 
          (conj (vec %1) %2)) 
        (conj (vec %1) %2)) [""] res)))

(defn- convert [ txt ]
  (if-let [kw (re-matches #"[_*](.+?)[_*]" txt)]
    [:em (second kw)]
    (if-let [kw (re-matches #"[_*]{2}(.+?)[_*]{2}" txt)]
      [:b (second kw)]
      (if-let [kw (re-matches #"\[(\w+)\]" txt)]
        (get mdicons (second kw))
        txt))))

(defn md [ txt ]
  (->> txt 
      (re-seq #"[_*]{1,2}.+?[_*]{1,2}|\[\w+\]|\w+|.")
      (map #(convert %))
      makespan))
      
;(ns markdown.markdown)

;; test string "Some text !£$%^&*()_+ [check] and some *bold1* more text\nNew line _Italic_ *BOLD:* [check] end"

;(def markdown_pattern #"([_*]{2}.+?[_*]{1,2})|(\[.+?\])|([a-zA-Z0-9\s\\\!\"\£\$\%\^\&\(\)\-\+\=\{\}\:\;\,\.\@\<\>\?\/]+)")
;;; note this excludes single character *_[]
;
;
;(defn convert
;"Converts single element to reagent"
;[pre txt]
;  (if-let [symbol (re-matches #"\[(\w+)\]" txt)]
;    [:span {:class (str pre (second symbol))}]
;    (if-let [italic (re-matches #"[_]{2}(.+?)[_]{1,2}" txt)]
;      (into [:em ] (second italic))
;      (if-let [italic (re-matches #"[*]{2}(.+?)[*]{1,2}" txt)]
;        (into [:b ] (second italic))
;        txt))))
;
;(defn parse 
;"Simple Markdown translated into reagent
;**bold**   [:b \"bold\"]
;__italic__ [:em \"italic\"]
;[symbol] [:span {:class \"pre symbol\"]
;TODO
;#h1 ##h2 ###h3 etc
;__**Bold and Italic**__"
;[pre txt]
;  (let [segments (remove empty? (clojure.string/split txt markdown_pattern))]
;    (map #(convert pre %) segments)))