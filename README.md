# 📝 Clojure Todo App

Welcome to the Clojure Todo App! This simple web application allows you to manage your todo list using a Clojure backend. The application supports adding, deleting, updating, and viewing todos. Additionally, it provides statistics on the total number of todos, as well as the number of completed and pending todos.

## 🚀 Getting Started

These instructions will help you set up and run the Clojure Todo App on your local machine.

### Prerequisites

Make sure you have the following installed:

- [Clojure](https://clojure.org/guides/getting_started)
- [Leiningen](https://leiningen.org/)
- [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

### Installation

1. **Clone the repository**:

   ```sh
   git clone https://github.com/Anton-Horn/Clojure-TODO.git
   cd Clojure-TODO
   ```

2. **Install dependencies**:

   ```sh
   lein deps
   ```

### Running the App

Start the application by running:

```sh
lein run
```

The server will start on port 3000. Open your browser and navigate to `http://localhost:3000` to access the Todo App.

## 🛠️ Project Structure

- **src/clojure_todo_app/core.clj**: Main application code, including route definitions and handler functions.
- **resources/public/js/app.js**: JavaScript file for updating statistics on the front end.
- **resources/public/css/style.css**: Custom styles for the application.

## 📋 API Endpoints

The application exposes the following API endpoints:

- `GET /todos`: Retrieve all todos.
- `POST /todos`: Add a new todo. The request body must include `title` (string) and `completed` (boolean).
- `DELETE /todos/:id`: Delete a todo by ID.
- `PATCH /todos/:id`: Update a todo by ID. The request body can include `title` and/or `completed`.
- `GET /statistics`: Retrieve statistics about todos, including total, completed, and pending counts.

## 📈 Statistics

The app displays real-time statistics for your todos:

- **Total**: Total number of todos.
- **Completed**: Number of completed todos.
- **Pending**: Number of pending todos.

These statistics are updated every 5 seconds.

## 🖥️ Frontend

The frontend of the application is built with HTML and JavaScript. It uses the following libraries:

- [Font Awesome](https://fontawesome.com/): Icons.
- [Pico CSS](https://picocss.com/): Minimal CSS framework.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.
