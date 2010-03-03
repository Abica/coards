(ns coards.url-helpers
  (:use (compojure.html gen page-helpers form-helpers)
        (coards utils)))

(defn boards-url [] "/boards.html")

(defn board-url [id]
  (str "/board/" id ".html"))

(defn create-board-url []
  "/admin/create-board.html")

(defn post-url [board-id post-id]
  (str "/board/" board-id "/post/" post-id ".html"))

(defn create-post-url [board-id]
  (str "/board/" board-id "/create-post.html"))
