(in-ns 'danuraisite.pages)

(defn hsl [ req ]
  (let [q (-> req :params :q)]
    (h/html5 
      (into pretty-head
        [(h/include-css "/css/hsl.css?v=0.1")])
      [:body
        (navbar req)
        [:div.container.my-3
          [:div#qry {:hidden false :data-query q}]
          [:div#app]]
       (h/include-js "/js/compiled/hsl-app.js")])))
     
(defn paintranges [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-3
        [:div#comparison.d-flex.mb-2]
        [:div
          [:table#colourtable]]]
      (h/include-css "//cdn.datatables.net/1.11.4/css/jquery.dataTables.min.css")
      (h/include-js  "//cdn.datatables.net/1.11.4/js/jquery.dataTables.min.js")
      ;<link rel="stylesheet" type="text/css" href="DataTables/datatables.min.css"/>
      ;<script type="text/javascript" src="DataTables/datatables.min.js"></script>
      (h/include-js  "/js/paintchart2.js")
      (h/include-css "/css/colour.css")
      ]))

(defn citadel-old [ req ]
  (h/html5 
    pretty-head
    [:style ".sample { cursor: pointer;} .sortable { cursor: pointer;}"]
    [:body
      (navbar req)
      [:div.container
        [:div.sticky-top.py-3.bg-light.row {:style "z-index: 1;"}
          [:div.col-12
            [:div.row.form-inline
              [:label.mr-2.my-auto "Filter by Range"]
              [:select#selectrange.mr-2 {:multiple true}]
              [:input#filter.form-control {:type "text" :placeholder "Filter Name"}]]
            [:div#comparison.row]]]
        [:div.row
          [:table.table.table-sm
            [:thead 
              [:tr 
                [:th.sortable "Name"]
                [:th.sortable "Range"]
                [:th.sortable "Match"]
                [:th.sortable "Hex"]
                [:th.sortable "hue"]
                [:th.sortable "sat"]
                [:th.sortable "lum"]
                [:th "Sample"]]]
            [:tbody#colours]]]]
      (h/include-js "https://cdnjs.cloudflare.com/ajax/libs/taffydb/2.7.2/taffy-min.js")
      (h/include-js "/js/paintchart.js?v=1.000")]))