(ns coronavirus-charts.events
  (:require
   [re-frame.core :as re-frame]
   [coronavirus-charts.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [ajax.core :as ajax]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 ::register-covid-data
 (fn-traced [db [_ data]]
            (assoc db :covid-data
                   (->> data
                        (map (fn [[country records]]
                               [country
                                (->> records
                                     (clojure.walk/keywordize-keys))]))))))

(re-frame/reg-event-fx
 ::load-covid-data
 (fn-traced [db _]
            {:db (assoc db :covid-data :loading)
             :http-xhrio {:method :get
                          :uri "https://pomber.github.io/covid19/timeseries.json"
                          :response-format (ajax/json-response-format)
                          :on-success [::register-covid-data]}}))
