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
            (submit-button "Submit")
            (reset-button "Reset"))]
        [:div.please-login "Please login to contribute!"])]))

(defn render-board [board]
  [:div.board-body
    (:message board)])

(defn render-board-item [board]
  (let [key (:encoded-key board)
        title (h (:title board))
        message (h (:message board))]
    [:div.topic-item
      [:h2 (link-to (board-url key) title)]
      [:p message]]))

(defn render-post-options [post]
  (let [key (:encoded-key post)]
    [:div.options
      [:span.option.delete
           (link-to
             (str (post-url key) "#/delete")
             "(delete)")]
      [:span.option.start-editing
            (link-to
              (str (post-url key) "#/edit")
              "(edit)")]
      [:span.option.save-post
            (link-to
              (str (post-url key) "#/edit/save")
              "(save and finish editing)")]
      [:span.option.close-editing
            (link-to
              (str (post-url key) "#/edit/cancel")
              "(cancel edit)")]]))

(defn render-full-post [post]
  (let [key (:encoded-key post)
        title (h (:title post))
        author (:author post)]
    [:div#post
      [:div.post-body
        [:pre
          (-> post :message h)]]
      [:p "Posted by " (.getNickname author) " on " (format-date post)]
      (if (owner? post) (render-post-options post))]))

(defn render-post-item [board post]
  (let [key (:encoded-key post)
        title (h (:title post))
        author (:author post)]
    [:div.topic-item
      [:h3 {:title (:message post)}
        (link-to (post-url key) title)]
      (if (owner? post) (render-post-options post))
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
    (link-to {:title (:message post)}
             (url-for post)
             (h (:title post)))
    " - " (.getNickname (:author post))
    (if (owner? post) (render-post-options post))
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
