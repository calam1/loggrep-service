(ns loggrep-service.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [loggrep-service.core.filter :as filter]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/filter" [] (filter/hardcoded-search-values))
  (GET "/search/:criteria/:search_date" [criteria search_date] (filter/grep-and-group-by-date-json-logexists criteria search_date))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
