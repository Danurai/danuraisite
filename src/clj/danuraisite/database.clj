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
  (j/delete! db :decklists ["author = ?" uid])
  (j/delete! db :users ["uid = ?" uid]))
  
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
             [:created  :date]]))
        (j/insert! db :sqlite_sequence {:name "users" :seq 1000})
        (j/insert! db :users {:username "root" :password (creds/hash-bcrypt "admin") :admin true  :created (t/now)})
        (j/insert! db :users {:username "dan"  :password (creds/hash-bcrypt "user")  :admin false :created (t/now)})
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
             [:created  :bigint]]))
        (j/insert! db :users {:username "root" :password (creds/hash-bcrypt "admin") :admin true  :created (c/to-long (t/now))})
        (j/insert! db :users {:username "dan"  :password (creds/hash-bcrypt "user")  :admin false :created (c/to-long (t/now))})
        (catch Exception e (println (str "DB Error - Users: " e))))))

(defn- create-tbl-version []
  (try
    (j/db-do-commands db
      (j/create-table-ddl :version
        [[:major    :int]
         [:minor    :int]
         [:note     :text]
         [:released :bigint]]))
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
           [:updated    :bigint]]))
      (j/insert! db :sqlite_sequence {:name "lugsparty" :seq 1000})
      (catch Exception e (println (str "DB Error - LUGS Party: " e))))
  ; Create Table in postgresql
    (try
      (j/db-do-commands db
        (j/db-do-commands db ["create sequence lp_uid_seq minvalue 1000"])
        (j/create-table-ddl :lugsparty
          [[:uid        :int :default "nextval ('lp_uid_seq')"]
           [:data       :text]  ;json
           [:author     :integer]
           [:created    :bigint]
           [:updated    :bigint]]))
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
           [:updated    :bigint]]))
      (j/insert! db :sqlite_sequence {:name "donvictims" :seq 1000})
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
           [:updated    :bigint]]))
      (catch Exception e (println (str "DB Error - DoN Victims: " e))))))
    
(defn create-db []
  (create-tbl-users)
  (create-tbl-version)
  (create-tbl-donvictims)
  (create-tbl-lugsparty)
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
          where-clause ["uid = ?" uid]]
      (j/with-db-transaction [t-con db]
        (let [result (j/update! t-con :donvictims qry where-clause)]
          (if (zero? (first result))
            (j/insert! t-con :donvictims (assoc qry :created (c/to-long (t/now))))
            result))))))

(defn get-user-victims [ author ]
  (j/query db ["SELECT * FROM donvictims WHERE author = ? ORDER BY UPDATED DESC" author]))
  
(defn get-victim [ vicuid ]
;; Should this also validate the logged in user? 
  (first (j/query db ["SELECT * FROM donvictims WHERE uid = ?" vicuid])))  
  
(defn remove-victim [ vicuid ]
  (j/delete! db  :donvictims ["uid = ?" vicuid]))
  
  
; Legends Untold: The Great Sewer

(defn save-party [ uid name data author ]
  (let [qry {:data data :author author :name name :updated (c/to-long (t/now))}
        where-clause ["uid = ?" uid]]
    (j/with-db-transaction [t-con db]
      (let [result (j/update! t-con :lugsparty qry where-clause)]
        (if (zero? (first result))
          (j/insert! t-con :lugsparty (assoc qry :created (c/to-long (t/now))))
          result)))))
          
(defn delete-party [ uid ]
  (j/delete! db :lugsparty ["uid = ?" uid]))
          
(defn get-user-parties [ author ]
  (j/query db ["SELECT * FROM lugsparty WHERE author = ? ORDER BY UPDATED DESC" author]))
  
(defn get-lugs-party [ author uid ]
  (j/query db ["SELECT * FROM lugsparty WHERE author = ? AND uid = ? ORDER BY UPDATED DESC" author uid]))
         
