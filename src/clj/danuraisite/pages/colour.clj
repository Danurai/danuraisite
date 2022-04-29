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
      [:div#filterModal.modal {:tabindex -1}
        [:div.modal-dialog
          [:div.modal-content 
            [:div.modal-header
              [:h4.modal-title "Filters"]
              [:button.btn-close {:type "button" :data-bs-dismiss "modal"} ]]
            [:div.modal-body
              [:div.d-flex [:div.form-check [:input#filter_owned.form-check-input {:type "checkbox"}] [:label.form-check-label "Owned"] ]]
              [:div.d-flex
                [:div.flex-fill.me-1
                  [:h5 "Brand"]
                  [:select#brandmulti.form-control {:multiple true :size 10}]]
                [:div.flex-fill 
                  [:h5 "Range"]
                  [:select#rangemulti.form-control {:multiple true :size 10}]]]]
            [:div.modal-footer
              [:button.btn.btn-primary {:type "button" :data-bs-dismiss "modal"} "OK"]]]]]
      [:div.container-fluid.my-3
        [:div#comparison.d-flex]
        [:div#matches.d-flex.mb-2]
        [:div
          [:table#colourtable]]]
      (h/include-css "//cdn.datatables.net/1.11.4/css/jquery.dataTables.min.css")
      (h/include-js  "//cdn.datatables.net/1.11.4/js/jquery.dataTables.min.js")
      ;<link rel="stylesheet" type="text/css" href="DataTables/datatables.min.css"/>
      ;<script type="text/javascript" src="DataTables/datatables.min.js"></script>
      (h/include-js  "/js/paintchart2.js")
      (h/include-css "/css/colour.css")]))

(defn painttbl [ req ]
  (let [cols (-> "private/paintlist.json" io/resource slurp (json/read-str :key-fn keyword))]
    (h/html5
      pretty-head
      [:div.container.my-3
        [:table#tbl.table
          [:thead [:tr [:th "Name"][:th "Range"][:th "OldGW"][:th "Hex"]]]
          [:tbody
            (for [clr cols]
              [:tr [:td (:name clr)][:td (:range clr)][:td (:oldgw clr)][:td (:hex clr)]])]
        ]]
      [:script "$(document).ready(function(){$('#tbl').DataTable({paging: false})});"]
      (h/include-css "//cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css")
      (h/include-js "//cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"))))