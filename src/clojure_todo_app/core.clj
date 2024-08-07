(ns clojure-todo-app.core
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response status]]))

(defrecord Todo [id title completed])

(defonce todos (atom []))

(defn valid-todo?
  "Checks if a todo item is valid by ensuring it contains :title and :completed keys,
  where :title is a string and :completed is a boolean."
  [todo]
  (and (contains? todo :title)
       (contains? todo :completed)
       (string? (:title todo))
       (boolean? (:completed todo))))

(defn get-todos
  "Returns a sorted list of todos wrapped in a response."
  []
  (let [sorted-todos (sort-by :id @todos)]
    (response sorted-todos)))

(defn next-id
  "Generates the next ID for a new todo item. If the todo list is empty, the next ID is 1.
  Otherwise, it is the maximum existing ID plus one."
  []
  (if (empty? @todos)
    1
    (inc (apply max (map :id @todos)))))

(defn add-todo
  "Adds a new todo item to the todo list. Validates the todo format before adding.
  If the todo is valid, assigns it a new ID and adds it to the list.
  Returns a 204 status on success or a 400 status if the todo format is invalid."
  [todo]
  (if (valid-todo? todo)
    (let [id (next-id)
          new-todo (->Todo id (:title todo) (:completed todo))]
      (swap! todos conj new-todo)
      (status (response nil) 204))
    (status (response {:error "Invalid Todo format"}) 400)))

(defn delete-todo
  "Deletes a todo item by ID. If the todo is found and removed, returns a 204 status.
  If the todo is not found, returns a 404 status."
  [id]
  (let [id (Integer. id)
        updated-todos (remove #(= id (:id %)) @todos)]
    (if (= (count @todos) (count updated-todos))
      (status (response {:error "Todo not found"}) 404)
      (do
        (reset! todos updated-todos)
        (status (response nil) 204)))))

(defn update-todo
  "Updates an existing todo item by ID with new data. If the todo is found and updated,
  returns a 204 status. If the todo is not found, returns a 404 status."
  [id todo-updates]
  (let [id (Integer. id)
        existing-todo (some #(when (= id (:id %)) %) @todos)]
    (if existing-todo
      (let [updated-todo (merge existing-todo todo-updates)
            updated-todos (mapv #(if (= id (:id %)) updated-todo %) @todos)]
        (reset! todos updated-todos)
        (status (response nil) 204))
      (status (response {:error "Todo not found"}) 404))))

(defn home-page []
  "Generates the HTML for the home page of the Todo app, including necessary CSS and JS files."
  (html5
    [:head
     [:title "Todo App"]
     (include-js "/js/app.js")
     (include-css "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css")
     (include-css "https://cdnjs.cloudflare.com/ajax/libs/picocss/2.0.6/pico.min.css")
     (include-css "/css/style.css")]
    [:body
     [:div.container
      [:nav
       [:ul
        [:li
         [:strong "Todo App"]]]]
      [:article.todos-wrapper
       [:p.no-todos "No todos yet \uD83D\uDE14"]
       [:fieldset.todos]
       [:p.statistics
        [:span.total "Total: "]
        [:span#total 0]
        [:span.completed " Completed: "]
        [:span#completed 0]
        [:span.pending " Pending: "]
        [:span#pending 0]]
       [:form#add-todo-form
        [:fieldset {:role "group"}
         [:input#title {:type "text" :placeholder "What's coming up today?" :autocomplete "off"}]
         [:button {:type "submit"}
          [:i.fa-solid.fa-circle-plus]]]]]]]))

(defn get-statistics
  "Returns statistics about todos, including the total count, the count of completed todos,
  and the count of pending todos."
  []
  (let [total (count @todos)
        completed (count (filter :completed @todos))
        pending (- total completed)]
    (response {:total total :completed completed :pending pending})))

(defroutes app-routes
           "Defines the routes for the Todo app."
           (GET "/" [] (home-page))
           (GET "/todos" [] (get-todos))
           (POST "/todos" req (add-todo (:body req)))
           (GET "/statistics" [] (get-statistics))
           (DELETE "/todos/:id" [id] (delete-todo id))
           (PATCH "/todos/:id" req (update-todo (:id (:params req)) (:body req)))
           (route/resources "/")
           (route/not-found (status (response nil) 404)))

(def app
  "Middleware stack for the Todo app, wrapping the routes with JSON response and request body handling."
  (-> app-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})))

(defn -main [& args]
  "Main entry point for the Todo app. Starts the Jetty server on port 3000."
  (run-jetty app {:port 3000 :join? false}))
