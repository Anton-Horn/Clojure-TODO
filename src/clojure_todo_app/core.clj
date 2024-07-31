(ns clojure-todo-app.core
  (:require [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response]]))

(defonce todos (atom []))

(defn get-todos []
  (response @todos))

(defn add-todo [todo]
  (swap! todos conj todo)
  (response {:status "success"}))

(defroutes app-routes
           (GET "/todos" [] (get-todos))
           (POST "/todos" req (add-todo (:body req)))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))

; TODO
; - Add a DELETE route
; - Add a PUT/PATCH route
; - Add statistics: number of todos, number of completed todos, number of uncompleted todos
; - Add a state to the todo: completed or uncompleted
; - Add a UI