(ns coards.url-helpers
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (coards layout utils)))

(defn boards-url [] "/boards.html")

(defn board-url [id]
  (str "/board/" id ".html"))

(defn create-board-url []
  "/admin/create-board.html")

(defn post-url [id]
  (str "/post/" id ".html"))

(defn create-post-url [board-id]
  (str "/board/" board-id "create-post.html"))
