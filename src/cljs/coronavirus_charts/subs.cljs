(ns coronavirus-charts.subs
  (:require
   [clojure.string :as string]
   [re-frame.core :as re-frame]
   ["moment" :as moment]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::covid-data-by-earliest-date
 (fn [{:keys [covid-data]}]
   (when (and covid-data (not (= :loading covid-data)))
     (->> covid-data
          :stat_by_country
          (map #(update % :record_date moment))
          (sort-by :record_date #(.isBefore %1 %2))
          (map (apply comp
                      (map (fn [keyword]
                             (fn [record] (update record keyword #(js/parseInt (clojure.string/replace % #"," "")))))
                           [:total_cases
                            :total_recovered
                            :new_deaths
                            :new_cases
                            :total_deaths
                            :active_cases
                            :serious_critical
                            :total_cases_per1m])))))))
