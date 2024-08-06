(ns clojure-todo-app.core
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response status]]))

(defrecord Todo [id title completed])

(defonce todos (atom []))

(defn valid-todo? [todo]
  (and (contains? todo :title)
       (contains? todo :completed)
       (string? (:title todo))
       (boolean? (:completed todo))))

(defn get-todos []
  (let [sorted-todos (sort-by :id @todos)]
    (response sorted-todos)))

(defn next-id []
  (if (empty? @todos)
    1
    (inc (apply max (map :id @todos)))))

(defn add-todo [todo]
  (if (valid-todo? todo)
    (let [id (next-id)
          new-todo (->Todo id (:title todo) (:completed todo))]
      (swap! todos conj new-todo)
      (status (response nil) 204))
    (status (response {:error "Invalid Todo format"}) 400)))

(defn delete-todo [id]
  (let [id (Integer. id)
        updated-todos (remove #(= id (:id %)) @todos)]
    (if (= (count @todos) (count updated-todos))
      (status (response {:error "Todo not found"}) 404)
      (do
        (reset! todos updated-todos)
        (status (response nil) 204)))))

(defn update-todo [id todo-updates]
  (let [id (Integer. id)
        existing-todo (some #(when (= id (:id %)) %) @todos)]
    (if existing-todo
      (let [updated-todo (merge existing-todo todo-updates)
            updated-todos (mapv #(if (= id (:id %)) updated-todo %) @todos)]
        (reset! todos updated-todos)
        (status (response nil) 204))
      (status (response {:error "Todo not found"}) 404))))

(defn home-page []
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
       [:form#add-todo-form
        [:fieldset {:role "group"}
         [:input#title {:type "text" :placeholder "What's coming up today?" :autocomplete "off"}]
         [:button {:type "submit"}
          [:i.fa-solid.fa-circle-plus]]]]]]]))

(defroutes app-routes
           (GET "/" [] (home-page))
           (GET "/todos" [] (get-todos))
           (POST "/todos" req (add-todo (:body req)))
           (DELETE "/todos/:id" [id] (delete-todo id))
           (PATCH "/todos/:id" req (update-todo (:id (:params req)) (:body req)))
           (route/resources "/")
           (route/not-found (status (response nil) 404)))

(def app
  (-> app-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))
