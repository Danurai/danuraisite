(in-ns 'danuraisite.pages)

(defn get-authentications [req]
  (#(-> (friend/identity %) :authentications (get (:current (friend/identity %)))) req))

(def pretty-head
  [:head
  ;; Meta Tags
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
  ;; icon
    [:link {:rel "icon" :href "/img/danuraisite.ico"}]
  ;; jQuery, Bootstrap and Popper
    [:link {
      :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" 
      :rel "stylesheet" 
      :integrity "sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We" 
      :crossorigin "anonymous"}]
    [:script {
      :src "https://code.jquery.com/jquery-3.4.1.min.js"
      :integrity "sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
      :crossorigin "anonymous"}]
    [:script {
      :src "https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js" 
      :integrity "sha384-U1DAWAznBHeqEIlVSCgzq+c9gqGAJn5c/t99JyeKa9xxaYpSvHU5awsuZVVFIhvj" 
      :crossorigin "anonymous"}]
    
  
  ;; Bootstrap Select
    [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.18/css/bootstrap-select.min.css"}]
    [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.18/js/bootstrap-select.min.js"}]
    [:script {:defer true :src "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/js/all.min.js"}]
    ;[:script "$.fn.selectpicker.Constructor.BootstrapVersion = '4';"]
  ;; fonts
    [:link {:href "https://fonts.googleapis.com/css?family=Orbitron|Exo+2|Eczar|Special+Elite&display=swap" :rel "stylesheet"}]
  ;; Site Specific
    ;; (h/include-css "/css/danuraisite.css?v=1")
    ])
    
  
(defn- navlink [ uri linkuri linkname ]
  [:li.nav-item {:class (if (= uri linkuri) "active")}
    [:a.nav-link {:href linkuri} linkname]])
  

(defn navbar [req]
  [:nav.navbar.navbar-expand-lg.navbar-dark.bg-dark {:style "z-index: 1021;"}
    [:div.container-fluid
    ;; Home Brand with Icon
      [:a.navbar-brand.mb-0.h1 {:href "/"} "Home"]
    ;; Collapse Button for smaller viewports
      [:button.navbar-toggler {:type "button" :data-bs-toggle "collapse" :data-bs-target "#navbarSupportedContent" 
                            :aria-controls "navbarSupportedContent" :aria-label "Toggle Navigation" :aria-expanded "false"}
        [:span.navbar-toggler-icon]]
    ;; Collapsable Content
      [:div#navbarSupportedContent.collapse.navbar-collapse
    ;; List of Links
        [:ul.navbar-nav
          [:li.nav-item.dropdown
            [:a.nav-link.dropdown-toggle {:href "#" :role "button" :data-bs-toggle "dropdown"} "LUGS"]
            [:div.dropdown-menu
              ;[:a.dropdown-item {:href "/lu"} "Sheets"]
              [:a.dropdown-item {:href "/lu/party"} "Party"]
              [:a.dropdown-item {:href "/lu/icons"} "Icons"]
              [:a.dropdown-item {:href "/lu/api"} "API"]]]
          [:li.nav-item.dropdown
            [:a.nav-link.dropdown-toggle {:href "#" :role "button" :data-bs-toggle "dropdown"} "Colours"]
            [:div.dropdown-menu
              [:a.dropdown-item {:href "/colours/hsl"} "HSL Demo"]
              [:a.dropdown-item {:href "/colours/paintranges"} "Paint Ranges"]]]
          (navlink (:uri req) "/don" "DoN Sheets")
          [:li.nav-item.dropdown
            [:a.nav-link.dropdown-toggle {:href "#" :role "button" :data-bs-toggle "dropdown"} "Netrunner"]
            [:div.dropdown-menu
              [:a.dropdown-item {:href "/netrunner/mwl"} "MWL Checker"]
              [:a.dropdown-item {:href "/netrunner/nrf"} "Virtual Folder"]]]
          (navlink (:uri req) "/scores" "Scores")
          [:li.nav-item.dropdown
            [:a.nav-link.dropdown-toggle {:href "#" :role "button" :data-bs-toggle "dropdown"} "Kill Team"]
            [:div.dropdown-menu
              [:a.dropdown-item {:href "/killteam/compendium"} "Compendium Reference"]
              [:a.dropdown-item {:href "/killteam/specops"} "Spec Ops Dataslate"]]]
          [:li.nav-item.dropdown
              [:a.nav-link.dropdown-toggle {:href "#" :role "button" :data-bs-toggle "dropdown"} "Misc."]
              [:div.dropdown-menu
                [:a.dropdown-item {:href "/scores"}  "Spirit Island Scores"]
                [:a.dropdown-item {:href "/ffqc"}    "FF Quick Combat"]
                [:a.dropdown-item {:href "/genesys"} "Genesys CharGen"]
                ]]]
    ;; Login Icon
        [:span.nav-item.dropdown.ms-auto
          [:a#userDropdown.nav-link.dropdown-toggle.text-white {:href "#" :role "button" :data-bs-toggle "dropdown"} [:i.fas.fa-user]]
              (if-let [identity (friend/identity req)]
                [:div.dropdown-menu {:aria-labelledby "userDropdown"}
                  (if (friend/authorized? #{::db/admin} (friend/identity req))
                    [:a.dropdown-item {:href "/admin"} "Admin Console"])
                  [:a.dropdown-item {:href "/logout"} "Logout"]]
                [:div.dropdown-menu {:aria-labelledby "userDropdown"}
                  [:a.dropdown-item {:href (str (if (= "/" (:uri req)) "" (:uri req)) "/login")} "Login"]])]]]])      
  