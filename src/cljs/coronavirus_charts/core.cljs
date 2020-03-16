(ns coronavirus-charts.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [coronavirus-charts.events :as events]
   [coronavirus-charts.routes :as routes]
   [coronavirus-charts.views :as views]
   [coronavirus-charts.config :as config]
   [day8.re-frame.http-fx]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/load-covid-data])
  (dev-setup)
  (mount-root))
