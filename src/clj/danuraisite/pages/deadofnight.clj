(in-ns 'danuraisite.pages)

     
(defn donsheets [req]
  (h/html5 
    (into pretty-head
      [(h/include-css "/css/don.css?v=0.1")])
    [:body
      (navbar req)
      [:div#donapp]
      (h/include-js "js/compiled/don-app.js")]))