(ns danuraisite.rklotrcore
  (:require
    [reagent.core :as r]
    [danuraisite.rklotrview :as view]))
    
(r/render [view/page] (.getElementById js/document "rklotrapp"))