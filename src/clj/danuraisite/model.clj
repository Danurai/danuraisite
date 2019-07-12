(ns danuraisite.model
  (:require 
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [cemerick.friend :as friend]
		[danuraisite.database :as db]))

(def alert (atom nil))
    
(defn get-authentications [req]
  (#(-> (friend/identity %) :authentications (get (:current (friend/identity %)))) req))

(defn apidata [ id ]
  (-> (str "private/" id ".json")
      io/resource
      slurp))

(def lugsicons
  (-> "private/lugs.json"
      io/resource
      slurp
      (json/read-str :key-fn keyword)
      :icons))
      
;; Dead of Night

; Victim/s api calls

(defn get-victims [ req ]
  (if-let [identity (friend/identity req)]
    (db/get-user-victims (-> req get-authentications :uid))
    "false"))
    
(defn save-victim [ data req ]
  (db/save-user-victim (-> req get-authentications :uid) data))
  
(defn get-victim [ id ]
  (db/get-victim id))
;  (if-let [identity (friend/identity req)]
;    (db/save-user-victim (-> identity :authentications (get (:current identity)) :id) (-> req :form-params :data))))
  
    