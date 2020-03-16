(ns coronavirus-charts.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [coronavirus-charts.subs :as subs]
   ["react-plotly.js" :default Plot]))

;; home


(defn home-panel []
  (let [covid-data (re-frame/subscribe [::subs/covid-data-by-earliest-date])]
    (when @covid-data
      (.log js/console (first @covid-data))
      [(reagent/adapt-react-class Plot)
       {:data [{:x (map (comp #(.toString %) :record_date) @covid-data)
                :y (map :total_cases @covid-data)
                :type "scatter"
                :mode "lines+markers"
                :marker {:color "blue"}
                :name "cases"
                :showlegend true}
               {:x (map (comp #(.toString %) :record_date) @covid-data)
                :y (map :total_deaths @covid-data)
                :type "scatter"
                :mode "lines+markers"
                :marker {:color "red"}
                :name "deaths"
                :showlegend true}
               {:x (map (comp #(.toString %) :record_date) @covid-data)
                :y (map :total_recovered @covid-data)
                :type "scatter"
                :mode "lines+markers"
                :marker {:color "green"}
                :name "recovered"
                :showlegend true}]
        :layout {:autosize true
                 :title "Covid-19 cases in France"}
        :config {:responsive true}
        :style {:width "100vw" :height "100vh"}}])))

;; about


(defn about-panel []
  [:div
   [:h1 "This is the About Page."]

   [:div
    [:a {:href "#/"}
     "go to Home Page"]]])


;; main


(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
