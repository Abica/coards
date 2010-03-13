(ns coards.url-helpers
  (:use (compojure.html gen page-helpers form-helpers)
        (coards utils)))

(defn boards-url [] "/boards.html")

(defn board-url [key]
  (str "/board/" key ".html"))

(defn post-url [key]
  (str "/post/" key ".html"))

(defn create-board-url []
  "/admin/create-board.html")

(defn create-post-url [key]
  (str "/board/" key "/create-post.html"))

(defn create-reply-url [key]
  (str "/post/" key "/create-post.html"))

(defn delete-post-url [key]
  (str "/post/" key "/delete-post.html"))

(defmulti url-for :kind)
(defmethod url-for "Post" [x] (post-url (:encoded-key x)))
(defmethod url-for "Board" [x] (board-url (:encoded-key x)))
(defmethod url-for :default [x] (boards-url))
