(in-ns 'danuraisite.pages)

(defn- app-page [ app req ]
  (h/html5 
    pretty-head
    [:body {:style "font-family: 'Orbitron', sans-serif; font-size: small;"}
      (navbar req)
      [:div#app]
      (h/include-js app)
      (h/include-js "/js/nrpopover.js")
      (h/include-css "/css/netrunnerfont.css")]))

(defn mwlpage [ req ]
  (app-page "/js/compiled/mwl-app.js" req))

(defn nrfpage [ req ]
  (app-page "/js/compiled/nrf-app.js" req))