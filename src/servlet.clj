(ns servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj datastore users)
        (coards layout utils url-helpers datastore-utils)))

(defn render-replies-for [post])

(defn- comment-form
  ([post]
    (comment-form "Add Comment" post))
  ([title post]
    [:div.comment-form
      [:h2 title]
      [:div#add-comment
        (form-to [:post (str "/add-comment/" (post :id))]
          [:p.post-title
            (label "title" "Title") [:br]
            (text-field "title")]
          [:p.post-body
            (label "body" "Body") [:br]
            (text-area {:rows 25 :cols 65} "body")]
          (submit-button "Submit"))]]))

(defn render-board [board]
  (let [id (:id board) title (:title board) desc (:desc board)]
    [:div.topic-item
      [:h2 (link-to (board-url id) title)]
      [:p desc]]))

(defn render-post [board post]
  (let [id (:id post) title (:title post) author (:author post)]
    [:div.topic-item
      [:h3 (link-to (post-url (:id board) id) title)]
      [:p "Posted by " author]]))

(defn render-posts-for [board]
  (let [posts (posts-for-board board)]
    [:div#posts
      (if (empty? posts)
        "There are no posts on this board. Be the first to create one!"
        (map (partial render-post board) posts))]))

;;;;;;;;;;;;;;;;;;;;;;;;;;
; controller methods
;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn list-boards []
  (layout "Message boards"
    [:div#boards
      (map render-board -all-boards)]))

(defn view-board [id]
  (when-let [board (find-board id)]
    (layout "Viewing board"
      [:h2 (:title board)]
      [:p (:desc board)]
      (render-posts-for board)
      (comment-form "Add Topic" board))))

(defn create-board [params]
  (println params)
  (redirect-to (post-url board-id post-id)))

(defn view-post [board-id post-id]
  (let [post (find-post board-id post-id)]
    (if post
      (layout "Viewing post"
        [:h2 (:title post)]
        [:p (:body post)]
        [:p "Posted by " (:author post)]
        (render-replies-for post)
        (comment-form "Post Reply" post))
      (page-not-found))))

(defn create-post [board-id]
  (layout "Message boards"
    [:h1 (str "Message board " board-id)]))

(defn create-reply-to-post [post-id]
  (layout "Message boards"
    [:h1 (str "FORM HERE " post-id)]))

(defn home [] (html [:h1 "Just you wait and see.."]))

(defroutes coards-app
  (GET  (boards-url)                  (list-boards))
  (GET  (board-url :id)               (view-board  (:id params)))
  (POST (create-post-url :board-id)   (create-post (:board-id params)))
  (GET  (post-url :board-id :post-id) (view-post   (:board-id params) (:post-id params)))
  (POST (create-board-url)            (create-board params))
  (GET "/"                            (home))

  ; static files and error handling
  (GET "/*" (or (serve-file (params :*)) :next))
  (ANY "*"  (page-not-found)))

;(decorate-with wrap-requiring-login list-boards)

(defservice (wrap-requiring-login (wrap-with-user-info coards-app)))
;(defservice (wrap-requiring-login coards-app))
