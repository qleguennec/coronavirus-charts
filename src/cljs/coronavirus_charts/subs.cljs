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
 ::get-covid-data
 (fn [{:keys [covid-data]}]
   (when (and covid-data (not (= :loading covid-data)))
     (->> covid-data
          (map (fn [[country records]]
                 [country
                  (->> records
                       (partition 2 1)
                       (map (fn [[first second]]
                              (merge second {:new-confirmed (- (:confirmed second) (:confirmed first))})))
                       (filter (fn [{:keys [new-confirmed]}] (> new-confirmed 100))))]))
          (sort-by first)))))
