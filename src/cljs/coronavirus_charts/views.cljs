(ns coronavirus-charts.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [coronavirus-charts.subs :as subs]
   ["react-plotly.js" :default Plot]))

;; home


(defn home-panel []
  (let [covid-data (re-frame/subscribe [::subs/get-covid-data])]
    (when @covid-data
      [(reagent/adapt-react-class Plot)
       {:data
        (map-indexed
         (fn [index [country records]]
           {:x (map :confirmed records)
            :y (map :new-confirmed records)
            :type "scatter"
            :mode "lines"
            :line {:color (/ index (count @covid-data))}
            :name (str "new cases in " country)
            :hovertext (map (fn [{:keys [date country confirmed]}] (str date " " country ": " confirmed " cases")) records)
            :hoverinfo "text"
            :showlegend true})
         @covid-data)
        :layout {:autosize true
                 :title "New Covid-19 cases per cases"
                 :xaxis {:type "log"}}
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
