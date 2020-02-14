(ns danuraisite.web
   (:require 
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [clj-http.client :as http]
    [compojure.core :refer [context defroutes GET ANY POST]]
    [compojure.route :refer [resources]]
    [ring.util.response :refer [response content-type redirect]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.session :refer [wrap-session]]
    [cemerick.friend :as friend]
    (cemerick.friend [workflows :as workflows]
                     [credentials :as creds])
    [hiccup.page :as h]
    [danuraisite.database :as db]
    [danuraisite.pages :as pages]
    [danuraisite.model :as model]
    ))
    

(defn- alert [ type message ]
  (reset! model/alert {:type type :message message}))
  
(defroutes admin-routes
  (GET "/" []
    pages/useradmin)
  (POST "/updatepassword" [uid password]
    (db/updateuserpassword uid password)
    (alert "alert-info" "Password updated")
    (redirect "/admin"))
  (POST "/updateadmin" [uid admin]
    (db/updateuseradmin uid (some? admin))
    (alert "alert-info" (str "Admin status " (if (some? admin) "added" "removed")))
    (redirect "/admin"))
  (POST "/adduser" [username password admin]
    (db/adduser username password (= admin "on"))
    (alert "alert-info" (str "User Account created for " username))
    (redirect "/admin"))
  (POST "/deleteuser" [uid]
    (alert "alert-warning" "User Account Deleted")
    (db/dropuser uid)
    (redirect "/admin")))
    
(defroutes api-routes 
  (GET "/data/:id" [id]
    (-> (model/apidata id)
        response
        (content-type "application/json"))))

;; LEGENDS UNTOLD
;;;;;;;;        
(defroutes lu-routes
  (context "/party/login" []
    (friend/wrap-authorize 
      (GET "/" [] (redirect "/lu/party"))
      #{::db/user}))
  (GET "/party" []
    pages/lugsparty)
  (GET "/api" []
    (redirect (str "/api/data/lu")))
  (GET "/api/parties" []
    #(-> (model/get-parties %)
         json/write-str
         response
         (content-type "application/json")))
  (GET "/api/party/:id" [id]
    #(-> (db/get-lugs-party (-> % model/get-authentications :uid) id)
         json/write-str
         response
         (content-type "application/json")))
  (GET "/icons" []
    pages/lugsicons)
  (POST "/party/save" [ uid name data ]
    #(response 
      (db/save-party uid name data (-> % model/get-authentications :uid))))
  (POST "/party/delete" [ uid ]
    (prn uid)
    (response (db/delete-party uid))))
    
;; DON
;;;;;;;;
(defroutes don-routes
  (GET "/" []
    pages/donsheets)
  (context "/login" []
    (friend/wrap-authorize
      (GET "/" [] (redirect "/don"))
      #{::db/user}))
  (GET "/api/victims" []
    #(-> (model/get-victims %)
         json/write-str
         response
         (content-type "application/json")))
  (GET "/api/victims/:id" [id]
    (-> (db/get-victim id)
        json/write-str
        response
        (content-type "application/json")))
;(friend/wrap-authorize pages/home #{::db/user}))
  (POST "/save" [data]
    #(response
      (db/save-user-victim (-> % model/get-authentications :uid) data)))
  (POST "/remove" [ uid ]
    (response (db/remove-victim uid))))
    
(defroutes colour-routes
  (GET "/hsl" [] pages/hsl)
  (GET "/citadel" [] pages/citadel))
  
(defroutes anr-routes
  (GET "/api/:id" [id]
    (if-let [f (io/resource (str "private/" id ".json"))]
      (-> f slurp response (content-type "application/json"))
      (-> "{\"data\": []}" response (content-type "application/json"))))
  (GET "/mwl" [] pages/mwlpage)
  (GET "/nrf" [] pages/nrfpage))
  
(defroutes dsapp-routes
  (GET "/"      [] pages/apphome)
  (GET "/kasei"  [] pages/kasei)
  (GET "/kosa"   [] pages/kosa)
  (GET "/rklotr" [] pages/rklotr))
  
(defroutes score-routes
  (GET "/"   [] pages/scorehome))
  
;; MAIN ROUTES
;;;;;;;;;;;;;;;;
(defroutes app-routes
  (GET     "/"         [] pages/homepage)
  (context "/api"       [] api-routes)
  (context "/lu"        [] lu-routes)
  (context "/colours"   [] colour-routes)
  (context "/don"       [] don-routes)
  (context "/netrunner" [] anr-routes)
  (context "/apps"  [] dsapp-routes)
  (context "/scores" [] score-routes)
; ADMIN
  (context "/:id/login" [id]
    (friend/wrap-authorize
      (GET "/" [] (redirect (str "/" id)))
      #{::db/user}))
  (GET "/login" []
    pages/login)
  (context "/admin" []
    (friend/wrap-authorize admin-routes #{::db/admin}))
  (POST "/register" [username password]
    (db/adduser username password false)
    (redirect "/"))
  (POST "/checkusername" [username] 
    (response (str (some #(= (clojure.string/lower-case username) (clojure.string/lower-case %)) (map :username (db/get-users))))))
  (friend/logout
    (ANY "/logout" [] (redirect "/")))
  (resources "/"))
  
(def friend-config {
  :allow-anon? true
  :login-uri "/login"
  :default-landing-uri "/"
  :unauthorised-handler (h/html5 [:body [:div.h5 "Access Denied " [:a {:href "/"} "Home"]]])
  :credential-fn #(creds/bcrypt-credential-fn (db/users) %)
  :workflows [(workflows/interactive-form)]})
  
(def app
  (-> app-routes
    (friend/authenticate friend-config)
    (wrap-keyword-params)
    (wrap-params)
    (wrap-session)))