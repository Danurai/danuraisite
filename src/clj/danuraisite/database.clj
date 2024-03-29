(ns danuraisite.database
  (:require 
    [clojure.java.jdbc :as j]
    [clojure.data.json :as json]
    [clj-time.core :as t]
    [clj-time.coerce :as c]
    [cemerick.friend :as friend]
      (cemerick.friend [credentials :as creds])))

; Role Hierarchy
(derive ::admin ::user)

; Define sqlite for local, or system (postgresql)
(def db (or (System/getenv "DATABASE_URL")
            {:classname   "org.sqlite.JDBC"
             :subprotocol "sqlite"
             :subname     "resources/db/db.sqlite3"}))

; Local postgresql for testing
;(def db {:dbtype "postgresql" :dbname "conq_db" :host "localhost" :port "5432" :user "conq_user" :password "user"})

;; admin
      
(defn updateuseradmin [uid admin]
  (j/db-do-commands db [(str "update users set admin=" admin " where uid=" uid)]))
;  (j/update! db :users {:admin (= admin 1)}
;           ["uid = ?" uid]))
  
(defn updateuserpassword [uid password]
  (j/db-do-commands db [(str "update users set password='" (creds/hash-bcrypt password) "' where uid=" uid)]))
;  (j/update! db :users {:password (creds/hash-bcrypt password)}
;           ["uid = ?" (int uid)]))
           
(defn adduser [username password admin]
  (j/insert! db :users 
    {:username username
     :password (creds/hash-bcrypt password)
     :admin admin
     :created (if (= (:subprotocol db) "sqlite")
                (t/now)
                (c/to-long (t/now)))}))

(defn dropuser [uid]
  (j/delete! db :decklists ["author = ?" (read-string uid)])
  (j/delete! db :users ["uid = ?" (read-string uid)]))

(defn create-sequence [ name ]
  (if (-> db :subprotocol (= "sqlite"))
    (if (empty? (j/query db ["select seq from sqlite_sequence where name = ?" name]))
      (j/insert! db :sqlite_sequence {:name name :seq 1000}))
    (try 
      (j/db-do-commands db [(str "create sequence " name "_uid_seq minvalue 1000")])
      (catch Exception e (println (str "Sequence for " name " already exists"))))))
  
(defn- create-tbl-users []
  (if (= (:subprotocol db) "sqlite") ; Split for AUTOINCREMENT / NEXTVAL
  ; Create User table in SQLITE
      (try
        (j/db-do-commands db 
          (j/create-table-ddl :users
            [[:uid      :integer :primary :key :AUTOINCREMENT]
             [:username :text]
             [:password :text]
             [:admin    :boolean]
             [:created  :date]]
            {:conditional? true}))
        (create-sequence "users")
        (if (empty? (j/query db ["select uid from users where username = ?" "root"]))
          (j/insert! db :users {:username "root" :password (creds/hash-bcrypt "admin") :admin true  :created (t/now)}))
        (if (empty? (j/query db ["select uid from users where username = ?" "dan"]))
          (j/insert! db :users {:username "dan"  :password (creds/hash-bcrypt "user")  :admin false :created (t/now)}))
        (catch Exception e (println (str "DB Error - Users: " e))))
  ; Create User Table in postgresql
      (try
        (j/db-do-commands db ["create sequence user_uid_seq minvalue 1000"])
        (j/db-do-commands db 
          (j/create-table-ddl :users
            [[:uid      :int :default "nextval ('user_uid_seq')"]
             [:username :text]
             [:password :text]
             [:admin    :boolean]
             [:created  :bigint]]
            {:conditional? true}))
        (if (empty? (j/query db ["select uid from users where username = ?" "root"]))
          (j/insert! db :users {:username "root" :password (creds/hash-bcrypt "admin") :admin true  :created (c/to-long (t/now))}))
        (if (empty? (j/query db ["select uid from users where username = ?" "root"]))
          (j/insert! db :users {:username "dan"  :password (creds/hash-bcrypt "user")  :admin false :created (c/to-long (t/now))}))
        (catch Exception e (println (str "DB Error - Users: " e))))))

(defn- create-tbl-version []
  (try
    (j/db-do-commands db
      (j/create-table-ddl :version
        [[:major    :int]
         [:minor    :int]
         [:note     :text]
         [:released :bigint]]
        {:conditional? true}))
    (j/insert! db :version {:major 0 :minor 1 :note "dev" :released (c/to-long (t/now))})
    (catch Exception e (str "DB Error - version: " e))))
    
(defn- create-tbl-lugsparty []
  (if (= (:subprotocol db) "sqlite") ; Split for AUTOINCREMENT / NEXTVAL
    (try 
      (j/db-do-commands db
        (j/create-table-ddl :lugsparty
          [[:uid        :integer :primary :key :AUTOINCREMENT]
           [:name       :text]
           [:data       :text]  ;json
           [:author     :integer]
           [:created    :bigint]
           [:updated    :bigint]]
          {:conditional? true}))
      (create-sequence "lugsparty")
      (catch Exception e (println (str "DB Error - LUGS Party: " e))))
  ; Create Table in postgresql
    (try
      (j/db-do-commands db
        (j/db-do-commands db ["create sequence lp_uid_seq minvalue 1000"])
        (j/create-table-ddl :lugsparty
          [[:uid        :int :default "nextval ('lp_uid_seq')"]
           [:data       :text]  ;json
           [:name       :text]
           [:author     :integer]
           [:created    :bigint]
           [:updated    :bigint]]
          {:conditional? true}))
      (catch Exception e (println (str "DB Error - LUGS Party: " e))))))
                       
(defn- create-tbl-donvictims []
  (if (= (:subprotocol db) "sqlite") ; Split for AUTOINCREMENT / NEXTVAL
  ; Create table in SQLITE
    (try
      (j/db-do-commands db
        (j/create-table-ddl :donvictims
          [[:uid        :integer :primary :key :AUTOINCREMENT]
           [:data       :text]  ;json
           [:author     :integer]
           [:created    :bigint]
           [:updated    :bigint]]
          {:conditional? true}))
      (create-sequence "donvictims")
      (catch Exception e (println (str "DB Error - DoN Victims: " e))))
  ; Create Table in postgresql
    (try
      (j/db-do-commands db ["create sequence don_uid_seq minvalue 1000"])
      (j/db-do-commands db
        (j/create-table-ddl :donvictims
          [[:uid        :int :default "nextval ('don_uid_seq')"]
           [:data       :text]  ;json
           [:author     :integer]
           [:created    :bigint]
           [:updated    :bigint]]
          {:conditional? true}))
      (catch Exception e (println (str "DB Error - DoN Victims: " e))))))
      
(defn- create-tbl-siscores []
  (j/db-do-commands db
    (j/create-table-ddl :siscores
      [[:players    :int]
       [:spirits    :text]
       [:boards     :text]
       [:adversary  :text]
       [:advlvl     :int]
       [:scenario   :text]
       [:difficulty :int]
       [:win        :boolean]
       [:invdeck    :int]
       [:dahan      :int]
       [:blight     :int]
       [:score      :int]
       [:date       :bigint]]
      {:conditional? true})))

(defn- create-tbl-specops []
  (if (= (:subprotocol db) "sqlite") ; Split for AUTOINCREMENT / NEXTVAL
  ; Create table in SQLITE
    (try
      (j/db-do-commands db
        (j/create-table-ddl :specops
          [
            [:uid        :integer :primary :key :AUTOINCREMENT]
            [:name       :text]
            [:faction    :text]
            [:selectable :text]
            [:base       :text]
            [:history    :text]
            [:quirks     :text]
            [:assetcap   :text]
            [:notes      :text]
            [:author     :integer]
            [:created    :bigint]
            [:updated    :bigint]]
          {:conditional? true}))
      (create-sequence "specops")
      (catch Exception e (println (str "DB Error - SpecOps: " e))))
  ; Create Table in postgresql
    (try
      (j/db-do-commands db ["create sequence specops_uid_seq minvalue 1000"])
      (j/db-do-commands db
        (j/create-table-ddl :specops
          [
            [:uid        :int :default "nextval ('specops_uid_seq')"]
            [:name       :text]
            [:faction    :text]
            [:selectable :text]
            [:base       :text]
            [:history    :text]
            [:quirks     :text]
            [:assetcap   :text]
            [:notes      :text]
            [:author     :integer]
            [:created    :bigint]
            [:updated    :bigint]]
          {:conditional? true}))
      (catch Exception e (println (str "DB Error - SpecOps: " e))))))
    
(defn- create-tbl-specops-requisitions []
  (create-sequence "specops_requisitions")
  (j/db-do-commands db
    (j/create-table-ddl :specops_requisitions
      (apply conj [
        (if (-> db :subprotocol (= "sqlite"))
            [:uid :integer :primary :key :AUTOINCREMENT]
            [:uid :integer :default "nextval ('specops_requisitions_uid_seq')"])
        ]
        [
          [:specop  :integer]
          [:type    :text]
          [:value   :text]
          [:note    :text]
          [:sort    :integer]
          [:created :bigint]])
      {:conditional? true})))
    
(defn- create-tbl-specops-specops []
  (create-sequence "specops_specops")
  (j/db-do-commands db
    (j/create-table-ddl :specops_specops
      (apply conj [
        (if (-> db :subprotocol (= "sqlite"))
            [:uid :integer :primary :key :AUTOINCREMENT]
            [:uid :integer :default "nextval ('specops_specops_uid_seq')"])
        ]
        [
          [:specop    :integer]
          [:name      :text]
          [:progress  :integer]
          [:rp        :integer]
          [:note      :text]
          [:created   :bigint]])
      {:conditional? true})))
    
(defn- create-tbl-specops-equipment []
  (create-sequence "specops_equipment")
  (j/db-do-commands db
    (j/create-table-ddl :specops_equipment
      (apply conj [
        (if (-> db :subprotocol (= "sqlite"))
            [:uid :integer :primary :key :AUTOINCREMENT]
            [:uid :integer :default "nextval ('specops_equipment_uid_seq')"])
        ]
        [
          [:specop    :integer]
          [:name      :text]
          [:ep        :integer]
          [:note      :text]
          [:created   :bigint]])
      {:conditional? true})))

(defn create-db []
  (create-tbl-users)
  (create-tbl-version)
  (create-tbl-donvictims)
  (create-tbl-lugsparty)
  (create-tbl-siscores)
  (create-tbl-specops)
  (create-tbl-specops-requisitions)
  (create-tbl-specops-specops)
  (create-tbl-specops-equipment)
  )
  
  
; USERS  
       
(defn users []
  (->> (j/query db ["select * from users"])
       (map (fn [x] (hash-map (:username x) (-> x (dissoc :admin)(assoc :roles (if (or (= 1 (:admin x)) (true? (:admin x))) #{::admin} #{::user}))))))
       (reduce merge)))

(defn get-users []
  (j/query db ["SELECT uid, username, admin FROM users"]))
  
(defn get-authentications [req]
  (#(-> (friend/identity %) :authentications (get (:current (friend/identity %)))) req))

; PAGES
  
;(defn update-or-insert!
;  "Updates columns or inserts a new row in the specified table"
;  [db table row where-clause]
;  (j/with-db-transaction [t-con db]
;    (let [result (j/update! t-con table row where-clause)]
;      (if (zero? (first result))
;        (j/insert! t-con table row)
;        result))))

; Dead of Night

;; uid/data/author/created/updated
(defn save-user-victim [ author data ] 
  (let [uid (:uid (json/read-str data :key-fn keyword))]
    (let [qry {:data data :author author :updated (c/to-long (t/now))}
          where-clause ["uid = ?" (read-string uid)]]
      (j/with-db-transaction [t-con db]
        (let [result (j/update! t-con :donvictims qry where-clause)]
          (if (zero? (first result))
            (j/insert! t-con :donvictims (assoc qry :created (c/to-long (t/now))))
            result))))))

(defn get-user-victims [ author ]
  (j/query db ["SELECT * FROM donvictims WHERE author = ? ORDER BY UPDATED DESC" author]))
  
(defn get-victim [ vicuid ]
;; Should this also validate the logged in user? 
  (first (j/query db ["SELECT * FROM donvictims WHERE uid = ?" (read-string vicuid)])))  
  
(defn remove-victim [ vicuid ]
  (j/delete! db  :donvictims ["uid = ?" (read-string vicuid)]))
  
  
; Legends Untold: The Great Sewer

(defn save-party [ uid name data author ]
  (let [qry {:data data :author author :name name :updated (c/to-long (t/now))}
        where-clause ["uid = ?" (if (nil? uid) nil (read-string uid))]]
    (j/with-db-transaction [t-con db]
      (let [result (j/update! t-con :lugsparty qry where-clause)]
        (if (zero? (first result))
          (j/insert! t-con :lugsparty (assoc qry :created (c/to-long (t/now))))
          result)))))
          
(defn delete-party [ uid ]
  (j/delete! db :lugsparty ["uid = ?" (read-string uid)]))
          
(defn get-user-parties [ author ]
  (j/query db ["SELECT * FROM lugsparty WHERE author = ? ORDER BY UPDATED DESC" author]))
  
(defn get-lugs-party [ author uid ]
  (j/query db ["SELECT * FROM lugsparty WHERE author = ? AND uid = ? ORDER BY UPDATED DESC" author uid]))
         
; Spirit Island Scores

(defn save-sidata [ data ]
  (j/insert! db :siscores (reduce-kv
    #(if (contains? #{"score" "advlvl" "players" "blight" "invdeck" "dahan" "difficulty"} %2)
         (assoc %1 %2 (read-string %3))
         (assoc %1 %2 %3))
    {} (assoc data :date (c/to-long (t/now)) ))
  ))

(defn getsiscores []
  (j/query db ["SELECT * FROM siscores ORDER BY date DESC"]))

(defn save-specops [ data ]
  (let [timestamp    (c/to-long (t/now))
        qry          (dissoc data :uid) 
        where-clause ["uid = ?" (if (-> data :uid empty?) nil (-> data :uid read-string))]]
    (j/with-db-transaction [t-con db]
      (let [result (j/update! t-con :specops (assoc qry :updated timestamp) where-clause)]
        (if (zero? (first result))
          (j/insert! t-con :specops (assoc qry  :created timestamp :updated timestamp))
          [data])))))

(defn getspecops [ ]
  (j/query db ["SELECT * from specops ORDER BY UPDATED DESC"]))

(defn getspecop [ uid ]
  (j/query db ["SELECT * from specops where uid = ? ORDER BY UPDATED DESC" (read-string uid)]))

(defn getrequisitions [ uid ]
  (j/query db ["SELECT * from specops_requisitions where specop = ? order by created desc" (read-string uid)]))

(defn- cast-field-toint [ data field ]
  (assoc data field (-> data field read-string) ))
(defn- add-field-date [ data field ]
  (assoc data field (c/to-long (t/now)) ))

(defn save-requisition [ data ]
  (let [qry (-> data (cast-field-toint :specop) (add-field-date :created))]
    (prn qry)
    (j/insert! db :specops_requisitions qry)))

(defn save-specops-specop [ data ]
  (let [qry (-> data (cast-field-toint :specop) (cast-field-toint :progress) (cast-field-toint :rp) (add-field-date :created))]
    (prn qry)
    (j/insert! db :specops_specops qry)))

(defn update-specops-specop [ data ]
  (j/db-do-commands db [(str "UPDATE specops_specops set progress = " (:progress data) " where uid = " (-> data :uid read-string))] ))

(defn getspecopsspecops [ uid ]
  (j/query db ["SELECT * from specops_specops WHERE specop = ? ORDER BY created DESC" (read-string uid)]))

(defn save-specops-equipment [ data ]
  (let [qry (-> data (cast-field-toint :specop) (cast-field-toint :ep) (add-field-date :created))]
    (prn qry)
    (j/insert! db :specops_equipment qry)))

(defn getspecopsequipment [ uid ]
  (j/query db ["SELECT * FROM specops_equipment WHERE specop = ? ORDER BY CREATED DESC" (read-string uid)]))