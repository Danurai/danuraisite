(in-ns 'danuraisite.pages)

(defn hsl [ req ]
  (let [q (-> req :params :q)]
    (prn (-> req :params))
    (h/html5 
      (into pretty-head
        [(h/include-css "/css/hsl.css?v=0.1")])
      [:body
        (navbar req)
        [:div.container.my-3
          [:div#qry {:hidden false :data-query q}]
          [:div#app]]
       (h/include-js "/js/compiled/hsl-app.js")])))
     
(defn citadel [ req ]
  (h/html5 
    pretty-head
    [:style ".sample { cursor: pointer;} .sortable { cursor: pointer;}"]
    [:body
      (navbar req)
      [:div.container
        [:div#comparison.row.sticky-top.bg-light.my-3]
        [:div.row.mb-3
          [:input#filter.form-control {:type "text" :placeholder "filter"}]]
        [:div.row
          [:table.table.table-sm
            [:thead 
              [:tr 
                [:th.sortable "Name"]
                [:th.sortable "Range"]
                [:th.sortable "Hex"]
                [:th.sortable "hue"]
                [:th.sortable "sat"]
                [:th.sortable "lum"]
                [:th "Sample"]]]
            [:tbody#colours]]]]
      (h/include-js "/js/paintchart.js?v=1.000")
      (h/include-js "/js/tablesort.js?v=1.000")
      ]))