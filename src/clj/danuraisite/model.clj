(ns danuraisite.model
  (:require 
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [clj-http.client :as http]
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
    
    
; Age of Sigmar: Champions

(defn aoscsearch [ size ]
  (http/post "https://carddatabase.warhammerchampions.com/warhammer-cards/_search" 
             {:content-type :json
              :body (json/write-str {:size size :from 1})}))

(defn aosccardcount []
  (-> (aoscsearch 1)
      :body 
      (json/read-str :key-fn keyword)
      :hits
      :total))