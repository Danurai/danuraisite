(ns danuraisite.lugscore 
  (:require 
    [reagent.core :as r]
    ;[reagent.dom.server :refer [render-to-string]]
    [danuraisite.lugsview :as view]
    ;[danuraisite.lugsmodel :as model]
    ))

;(model/init!)
;
;
;;(r/render [view/Page] (.getElementById js/document "app"))
;(defn tooltip [ msg ]
;  (r/create-class 
;    {:component-did-mount #(.tooltip (js/$ (r/dom-node %)))
;     :reagent-render (fn [msg] [:button.btn.btn-dark.mr-2 {:title msg} "Tooltip"])}))
;
;(defn popover [ msg ]
;  (r/create-class 
;    {:component-did-mount #(.popover (js/$ (r/dom-node %)) (clj->js {:trigger "hover"}))
;     :reagent-render (fn [msg] [:button.btn.btn-dark.mr-2 {:title msg} "PopOver"])}))
;

(defn home []
  (r/create-class
    {:component-did-mount 
      (fn [c]
        (.popover (js/$ (r/dom-node c)) 
          (clj->js {:trigger "hover" :position "auto" :selector ".showpopover"  :content "popover content" :title "Title"})))
     :reagent-render (view/Page)}))
    
(r/render [view/Page] (.getElementById js/document "app"))
