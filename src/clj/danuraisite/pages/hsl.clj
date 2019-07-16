(in-ns 'danuraisite.pages)

(defn hsl [ req ]
  (h/html5 
    (into pretty-head
      [(h/include-css "/css/hsl.css?v=0.1")])
    [:body
      (navbar req)
      [:div.container.my-2
        [:div#app]]
     (h/include-js "/js/compiled/hsl-app.js")]))