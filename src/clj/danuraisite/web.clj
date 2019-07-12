(ns danuraisite.web
   (:require 
    [clojure.java.io :as io]
    [clojure.data.json :as json]
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
  (GET "/" []
    pages/api)
  (GET "/data/:id" [id]
    (-> (model/apidata id)
        response
        (content-type "application/json"))))
  
(defroutes lugs-routes
  (GET "/" []
    pages/lugs)
  (GET "/api" []
    pages/api)
  (GET "/icons" []
    pages/lugsicons))
    
  
(defroutes don-routes
  (GET "/" []
    pages/donsheets)
  (GET "/api/victims" []
    #(-> (model/get-victims %)
         json/write-str
         response
         (content-type "application/json")))
  (GET "/api/victims/:id" [id]
    (-> (model/get-victim id)
        json/write-str
        response
        (content-type "application/json")))
  (POST "/save" [data]
    #(response (model/save-victim data %))))
    ;(friend/wrap-authorize pages/home #{::db/user}))
  
(defroutes app-routes
  (GET "/" [] 
    ;(friend/wrap-authorize pages/home #{::db/user}))
		pages/homepage)
  (context "/api" []
    api-routes)
  (context "/lugs" []
    lugs-routes)
  (GET "/hsl" []
    pages/hsl)
  (context "/don" []
    don-routes)
; ADMIN
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
  
(def app
  (-> app-routes
     (friend/authenticate
      {:allow-anon? true
       :login-uri "/login"
       :default-landing-uri "/"
       :unauthorised-handler (h/html5 [:body [:div.h5 "Access Denied " [:a {:href "/"} "Home"]]])
       :credential-fn #(creds/bcrypt-credential-fn (db/users) %)
       :workflows [(workflows/interactive-form)]})
    (wrap-keyword-params)
    (wrap-params)
    (wrap-session)))