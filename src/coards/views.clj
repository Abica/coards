(ns coards.views
  (:use (compojure.html gen page-helpers form-helpers)
        (appengine-clj datastore users)
        (coards utils url-helpers models)))

(defn render-comment-form
  ([post]
    (render-comment-form (str "/add-comment/" (post :encoded-key)) "Add Comment" post))
  ([path title post]
    [:div.comment-form
      [:h2 title]
      (if (logged-in?)
        [:div#add-comment
          (form-to [:post path]
            [:p.post-form-title
              (label "title" "Title") [:br]
              (text-field "title")]
            [:p.post-form-body
              (label "body" "Body") [:br]
              (text-area {:rows 25 :cols 65} "body")]
            (submit-button "Submit"))]
        [:div.please-login "Please login to contribute!"])]))

(defn render-board [board]
  [:div.board-body
    (:message board)])

(defn render-board-item [board]
  (let [id (:encoded-key board)
        title (h (:title board))
        message (h (:message board))]
    [:div.topic-item
      [:h2 (link-to (board-url id) title)]
      [:p message]]))

(defn render-full-post [post]
  (let [id (:encoded-key post)
        title (h (:title post))
        author (:author post)]
    [:div#post
      [:div.post-body (h (:message post))]
      [:p "Posted by " (.getNickname author) " on " (format-date post)]]))

(defn render-post-item [board post]
  (let [id (:encoded-key post)
        title (h (:title post))
        author (:author post)]
    [:div.topic-item
      [:h3 (link-to (post-url id) title)]
      [:p "Posted by " (.getNickname author) " on " (format-date post)]]))

(defn render-posts-for [board]
  (let [posts (posts-for board)]
    [:div#posts
      (if (empty? posts)
        "There are no posts on this board. Be the first to create one!"
        (map (partial render-post-item board) posts))]))

(declare render-tree)

(defn render-node-link [post]
  [:li
    (link-to (url-for post)
             (h (:title post)))
    " - " (.getNickname (:author post))
    (-> post posts-for render-tree)])

(defn render-tree [nodes]
  [:ul
   (map render-node-link nodes)])

(defn render-replies-for [post]
  (let [posts (posts-for post)]
    [:div#replies
      [:strong "Replies to this post"]
      (if (empty? posts)
        [:p "There are no replies to this post. Be the first! Join the discussion!"]
        (render-tree posts))]))
