(ns coards.url-helpers
  (:use (compojure.html gen page-helpers form-helpers)
        (coards utils)))

(defn boards-url [] "/boards.html")

(defn board-url [id]
  (str "/board/" id ".html"))

(defn post-url [post-id]
  (str "/post/" post-id ".html"))

(defn create-board-url []
  "/admin/create-board.html")

(defn create-post-url [board-id]
  (str "/board/" board-id "/create-post.html"))

(defn create-reply-url [post-id]
  (str "/post/" post-id "/create-post.html"))

(defmulti url-for :kind)
(defmethod url-for "Post" [x] (post-url (:id x)))
(defmethod url-for "Board" [x] (board-url (:id x)))
(defmethod url-for :default [x] (boards-url))
