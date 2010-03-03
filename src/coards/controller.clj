(ns coards.controller
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj datastore users)
        (coards views layout utils url-helpers models)))

(defn list-boards [request]
  (layout request "Message boards"
    [:div#boards
      (map render-board (find-boards))]))

(defn view-board [request id]
  (when-let [board (find-board id)]
    (layout request "Viewing board"
      (render-board board)
      (render-posts-for board)
      (render-comment-form (create-post-url id) "Add Topic" board))))

(defn view-post [request board-id post-id]
  (let [board (find-board board-id)
        post (find-post board-id post-id)]
    (if post
      (layout request "Viewing post"
        (render-full-post board post)
        (render-replies-for post)
        (render-comment-form (create-post-url board-id) "Post Reply" post))
      (page-not-found))))

(defn create-board [request params]
  (a-create-board (:user (:appengine-clj/user-info request))
                  (:title params)
                  (:body params))
  (redirect-to (boards-url)))
 ; (redirect-to (post-url board-id post-id)))
  ;

(defn create-post [request params board-id]
  (let [board (find-board board-id)]
    (a-create-post (:user (:appengine-clj/user-info request))
                   board
                   (:title params)
                   (:body params))
    (redirect-to (board-url board-id))))

(defn create-reply-to-post [post-id]
  (layout "Message boards"
    [:h1 (str "FORM HERE " post-id)]))

(defn home [] (html [:h1 "Just you wait and see.."]))
