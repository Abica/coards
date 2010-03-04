(ns coards.controller
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj datastore users)
        (coards views layout utils url-helpers models)))

(defn list-boards [request]
  (layout request "Message boards"
    [:div#boards
      (map render-board (find-boards))]
    (render-comment-form (create-board-url) "Add Board" nil)))

(defn view-board [request id]
  (let [board (find-board id)]
    (if board
      (layout request "Viewing board"
        [:div#breadcrumb (breadcrumb-trail-for nil)]
        (render-board board)
        (render-posts-for board)
        (render-comment-form (create-post-url id) "Add Topic" board))
      (page-not-found))))

(defn view-post [request post-id]
  (let [post (find-post post-id)]
    (if post
      (layout request "Viewing post"
        [:div#breadcrumb (breadcrumb-trail-for post)]
        (render-full-post post)
        (render-replies-for post)
        (render-comment-form (create-reply-url post-id) "Post Reply" post))
      (page-not-found))))

(defn create-board [request params]
  (redirect-to
    (board-url
      (:id (do-create-board (:user (:appengine-clj/user-info request))
                            (:title params)
                            (:body params))))))

(defn create-post [request params find-parent-func parent-id]
  (let [parent (find-parent-func parent-id)]
    (redirect-to
      (post-url
        (:id (do-create-post (:user (:appengine-clj/user-info request))
                             parent
                             (:title params)
                             (:body params)))))))

(defn home [] (html [:h1 "Just you wait and see.."]))
