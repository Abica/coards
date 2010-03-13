(ns coards.controller
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj datastore users)
        (coards views layout utils url-helpers models)))

(defn list-boards [request]
  (layout request "Message boards"
    [:div#boards
      (map render-board-item (find-boards))]
      (if (admin?)
          (render-comment-form (create-board-url) "Add Board" nil))))

(defn view-board [request encoded-key]
  (let [board (find-object encoded-key)]
    (if board
      (layout request "Viewing board"
        [:div#breadcrumb (breadcrumb-trail-for board)]
        (render-board board)
        (render-posts-for board)
        (render-comment-form (create-post-url encoded-key) "Add Topic" board))
      (page-not-found))))

(defn view-post [request encoded-key]
  (let [post (find-object encoded-key)]
    (if post
      (layout request "Viewing post"
        [:div#breadcrumb (breadcrumb-trail-for post)]
        (render-full-post post)
        (render-replies-for post)
        (render-comment-form (create-reply-url encoded-key) "Post Reply" post))
      (page-not-found))))

(defn create-board [request params]
  (if (admin?)
    (redirect-to
      (board-url
        (:encoded-key (do-create-board (:user (:appengine-clj/user-info request))
                                       (:title params)
                                       (:body params)))))
    (page-not-found)))

(defn create-post [request params parent-id]
  (let [parent (find-object parent-id)]
    (redirect-to
      (post-url
        (:encoded-key (do-create-post (:user (:appengine-clj/user-info request))
                                      parent
                                      (:title params)
                                      (:body params)))))))

(defn delete-post [request params encoded-key]
  (let [parent (find-object encoded-key)]
    (do-delete-post encoded-key)
    (-> encoded-key
        get-key
        .getParent
        url-for
        redirect-to)))
