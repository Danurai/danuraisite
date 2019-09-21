(ns nisei.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require 
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))
    
; virtual folders

(def ^:const imgurltemplate "https://netrunnerdb.com/card_image/{code}.png")

(def cards    (r/atom nil))
(def cardlist (r/atom nil))

(go
  (reset! cards    (-> (<! (http/get "/api/cards"))   :body :data))
  (reset! cardlist (->> (-> (<! (http/get "/api/cards"))   :body :data)
                      ;(filter #(= (:pack_code %) "core"))  
                      (filter #(= (:faction_code %) "anarch"))  
                      (filter #(= (:type_code %) "program")))))

(defn App []
  (let [pageno (r/atom 1)]
    (fn []
      [:div.container-fluid.my-3
        [:span (-> @cardlist count)]
        [:div.row
          [:div.col-sm-4]
          [:div.col-sm-8
            [:div.row.mb-3
              [:div.btn-group.btn-group-sm ;.btn-group-toggle.btn-group-sm {:data-toggle "buttons" :on-change #(prn (-> % .-target }
                (doall (for [n (range 1 (-> @cardlist count (/ 9) Math/ceil inc))]
                  ^{:key (gensym)}[:button.btn.btn-outline-secondary {:class (if (= n @pageno) "active") :on-click #(reset! pageno n)}
                    n]))]]
            [:div.row.mb-3
              (for [c (take 9 (-> @cardlist (nthnext (-> @pageno dec (* 9)))))]
                ^{:key (gensym)}[:div.col-4.mb-3
                    [:img.img-fluid {:src (if-let [img (:image_url c)] img (clojure.string/replace imgurltemplate #"\{code\}" (:code c)))}]])]
          ]]])))

(r/render [App] (.getElementById js/document "app"))