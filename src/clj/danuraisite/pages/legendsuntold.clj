(in-ns 'danuraisite.pages)

(defn lugsparty [ req ]
  (h/html5
    pretty-head
    [:body 
      (navbar req)
      ;[:div "DEBUG"]
      ;[:div (-> req model/get-authentications str)]
      ;[:div (db/get-user-parties (-> req model/get-authentications :uid))]
      [:div#app]
      (h/include-css "/css/rpg-awesome.min.css?v=1")
      (h/include-js "/js/compiled/lugs-app.js")]))
  
(defn lugsicons [ req ]
  (h/html5
    (into pretty-head
      [(h/include-css "/css/rpg-awesome.min.css")]) ;RPG_AWESOME
    [:body
      (navbar req)
      [:div.container.my-2
        [:table.table.table-sm {:style "width: auto;"}
          [:thead [:tr [:th "Name"][:th "Set"][:th "Icon"][:th "Icon name"]]]
          [:tbody#icontable]]]
    (h/include-js "/js/icontable.js")]))