(ns coards.views
  (:use (compojure.html gen page-helpers form-helpers)
        (appengine-clj datastore users)
        (coards utils url-helpers models)))

(defn render-comment-form
  ([post]
    (render-comment-form (str "/add-comment/" (post :id)) "Add Comment" post))
  ([path title post]
    [:div.comment-form
      [:h2 title]
      (if (logged-in?)
        [:div#add-comment
          (form-to [:post path]
            [:p.post-title
              (label "title" "Title") [:br]
              (text-field "title")]
            [:p.post-body
              (label "body" "Body") [:br]
              (text-area {:rows 25 :cols 65} "body")]
            (submit-button "Submit"))]
        [:div.please-login "Please login to contribute!"])]))

(defn render-board [board]
  (let [id (:id board)
        title (:title board)
        message (:message board)]
    [:div.topic-item
      [:h2 (link-to (board-url id) title)]
      [:p message]]))

(defn render-full-post [post]
  (let [id (:id post) title (:title post) author (:author post)]
    [:div#post
      [:h3.post-header title]
      [:div.post-body (:message post)]
      [:p "Posted by " (.getNickname author)]]))

(defn render-post-item [board post]
  (let [id (:id post) title (:title post) author (:author post)]
    [:div.topic-item
      [:h3 (link-to (post-url id) title)]
      [:p "Posted by " (.getNickname author)]]))

(defn render-posts-for [board]
  (let [posts (posts-for board)]
    [:div#posts
      (if (empty? posts)
        "There are no posts on this board. Be the first to create one!"
        (map (partial render-post-item board) posts))]))

(defn render-replies-for [post]
  (let [posts (posts-for post)]
    [:div#replies
      [:ul
        (map
          (fn [p] [:li
                    (link-to (url-for p)
                             (:title p))])
           posts)]]))
