(in-ns 'danuraisite.pages)


(defn aosc-table [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div.container
        [:div.row.my-2
          [:input#filter.form-control {:placeholder "Filter by name"}]]
        [:div.row
          [:table#cardtable.table.table-sm.table-hover
            [:thead
              [:tr
                [:th.sortable {:scope "col"} "#"]
                [:th.sortable {:scope "col"} "Collector #"]
                [:th.sortable {:scope "col"} "Name"]
                [:th.sortable {:scope "col"} "Category"]
                [:th.sortable {:scope "col"} "Alliance"]
                [:th.sortable {:scope "col"} "Class"]  
                [:th {:scope "col"} "Corners"]
                [:th.sortable {:scope "col"} "Wave"]
                ]]
            [:tbody#tblbody]]]]]
      (h/include-js "/js/aosc/carddatabase.js")))
            
(defn aosc-tools [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-2
        [:div (:uri req)]]]))
        
(defn aosc-tooltips [ req ]
  (h/html5
    (into pretty-head
      [[:title "Example of Warhammer Ago of Sigmar: Champions card tooltips"]
       (h/include-js "/js/externs/warhammer-card-tooltip.min.js")])
    [:body
      (navbar req)
      [:div.container.my-2
        [:h1 "Basic Usage"]
        [:p "Lorem ipsum " 
          [:span.warhammer-card-name "Aetherwing Scout"] 
          " sit amet, " 
          [:span.warhammer-card-name {:data-card-name "Abjuration"} "consectetur adipiscing elit"]
          ". The card "
          [:span.warhammer-card-name "Wibbly Wobblechops"]
          " doesn't exist, so it is not highlighted."]
        [:script "warhammerCardTooltip.init();"]
        [:h1 "Advanced Usage"]
        [:ul
          [:li#find-card-links "Find card links using a different function: " [:span "Aetherwing Scout"]]
          [:li#get-card-name "Use an alternate method of getting card names: " [:span "Badger" ] " and " [:span "Mushroom"]]
          [:li#activate-card-link> "Different logic to mark a link as activated: " [:span "Aetherwing Scout"]]
          [:li#create-tooltip> "Create an alternate tooltip structure: " [:span "Charging Black Knight"]]
          [:li#no-tooltip> "Don't create a tooltip at all, use the returned data to decorate the name: " 
            [:span "Aspect of the Sea"] ", " [:span "Akhelian Barrier Guard"] ", " [:span "Battle Glory"]]
          [:li#link-to-database "Link to card database: " [:span "Bellowing Blade"]]
          [:lipopper-options "Change the position of the tooltip: " [:span "Carrion Feast"]]]]
        (h/include-js "/js/aosc/aosc-tooltips.js?v=1")]))
  
(defn aosc-api [ req ]
  (h/html5
    pretty-head
    [:body
      (navbar req)
      [:div.container.my-2
        [:div (:uri req)]]]))
        

      