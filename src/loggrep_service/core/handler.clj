(ns loggrep-service.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [loggrep-service.core.filter :as filter]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/filter" [] (filter/hardcoded-search-values))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
