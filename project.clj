(defproject clojure-todo-app "0.1.0-SNAPSHOT"
  :description "A simple todo app written in clojure."
  :url "https://github.com/Anton-Horn/Clojure-TODO"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.2"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [hiccup "2.0.0-RC3"]]
  :repl-options {:init-ns clojure-todo-app.core}
  :main ^:skip-aot clojure-todo-app.core )
