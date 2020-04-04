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
                       (map (fn [records]
                              (assoc (second records)
                                     :new-confirmed
                                     (- (:confirmed (second records))
                                        (:confirmed (first records))))))
                       (filter (fn [{:keys [new-confirmed confirmed]}]
                                 (> new-confirmed (.. js/Math (sqrt confirmed)))))
                       (map #(update % :date moment)))]))
          (filter (fn [[country records]] (not-empty records)))
          (sort-by first)))))

(re-frame/reg-sub
 ::get-earliest-record-date
 :<- [::get-covid-data]
 (fn [covid-data]
   (map (comp :date first second) covid-data)))

(re-frame/reg-sub
 ::get-domain-trend
 :<- [::get-covid-data]
 (fn [covid-data [_ domain]]
   (when (and covid-data (not (= :loading covid-data)))
     (let [lerps (->> covid-data
                      (mapcat (fn [[_ records]] (partition 2 1 records)))
                      (map (fn [[rec1 rec2]]
                             {:start (:confirmed rec1)
                              :end (:confirmed rec2)
                              :a (/ (- (:new-confirmed rec2) (:new-confirmed rec1))
                                    (- (:confirmed rec2) (:confirmed rec1)))})))]
       (->> domain
            (map (fn [x]
                   (let [a-values
                         (->> lerps
                              (filter (fn [{:keys [start end]}] (and (< start x)
                                                                     (>= end x))))
                              (map :a))]
                     [x (* x (/ (reduce + a-values) (count a-values)))])))
            (filter (fn [[x y]] (>= y 0))))))))
