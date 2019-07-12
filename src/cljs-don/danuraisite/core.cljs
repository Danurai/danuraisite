(ns danuraisite.core
  (:require 
    [reagent.core :as r]
    [danuraisite.view :as view]
    [danuraisite.model :as model]))
  
(model/reset-don!)

(r/render [view/don] (.getElementById js/document "donapp"))

(.tooltip (js/$ ".show-tooltip"))