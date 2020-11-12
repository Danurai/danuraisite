(in-ns 'danuraisite.pages)

(defn lugsparty [ req ]
  (h/html5
    pretty-head
    [:body 
      (navbar req)
      [:div#app]
      (h/include-css "/css/rpg-awesome.min.css?v=1")
      (h/include-js "/js/compiled/lugs-app.js")]))

(defn lugs [ req ]
  (h/html5
    (into pretty-head
      [(h/include-css "/css/lugs.css?v=0.1")])
    [:body
      (navbar req)
      [:div.container.my-2.lugs
        [:div.row
          [:div.col-md-5
            [:div.row.mb-2
              [:h3.text-secondary "Hero"]
              [:input#hname.form-control {:type "text" :placeholder "Name"}]]
            [:div#hdata.row.mb-2]
            [:div.row.mb-2
              [:form.form-inline
                [:label.mr-2 "Level"]
                [:select#charlvl.mr-2.form-control
                  (for [x (range 1 9)]
                    [:option x])]
                ;[:input#charlvl.form-control-range.mr-2 {:type "range" :min "1" :value "1" :max "8"}]
                [:button#lonewolf.btn.btn-outline-secondary.float-right {:data-toggle "button"} [:span.mr-1 "Lone Wolf"] [:i.ra.ra-wolf-howl]]]]
          ]
          [:div.col-md-7
            [:div.row.mb-2.justify-content-center
              [:div#optionbuttons.btn-group.btn-group-sm.btn-group-toggle {:data-toggle "buttons"}
                [:label.btn.btn-outline-secondary.active [:input#occupation {:type "radio" :name "opt" :checked true} "Occupation"]]
                [:label.btn.btn-outline-secondary [:input#weaponupgrade {:type "radio" :name "opt"} "Weapon"]]
                [:label.btn.btn-outline-secondary [:input#weapontalent {:type "radio" :name "opt"} "Weapon Talent"]]
                [:label.btn.btn-outline-secondary [:input#skilltalent {:type "radio" :name "opt"} "Skill Talent"]]
                [:label.btn.btn-outline-secondary [:input#outfitupgrade {:type "radio" :name "opt"} "Outfit"]]
                [:label.btn.btn-outline-secondary [:input#kitupgrade {:type "radio" :name "opt"} "Kit"]]]]
            [:div#cardlist.row]]]]]
    (h/include-js "/js/lugs.js?v=0.2")
  ))
  
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