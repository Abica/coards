(ns coards.layout
  (:use (compojure.html gen page-helpers form-helpers)
        (appengine-clj datastore users)
        (coards url-helpers)))

(defn breadcrumb-link-for [object]
  (let [title (h (:title object))]
    [:span.breadcrumb
      (if (:text-only object)
          title
          (link-to (url-for object) title))]))

(defn breadcrumb-trail-for [object]
  (let [parents
          (take-while #(not (nil? %))
                       (iterate #(.getParent
                                   (or (:key %) %))
                                object))]
     (interpose " > "
                 (map breadcrumb-link-for
                      (concat [{:title "Boards"}]
                              (if (empty? parents)
                                  []
                                  (concat (map get-entity
                                              (reverse (rest parents)))
                                          [(assoc (first parents)
                                                  :text-only true)])))))))

(defn render-login-link [request]
  (let [info (:appengine-clj/user-info request)
        user (:user info)
        user-service (:user-service info)]
    (if (logged-in? info)
       [:span.user "Logged in as " (.getNickname user) " ("
         (link-to (.createLogoutURL user-service (:uri request)) "logout") ")"]
       [:span.user (link-to (.createLoginURL user-service (:uri request)) "login to comment")])))

(defn layout
  ([request title & body]
    (html
      (doctype :xhtml-strict)
      (xhtml-tag "en"
        [:head
          (include-css "http://github.com/Abica/coards/raw/master/war/public/css/main.css")
          (include-js "/js/jquery-1.4.2.js")
          (include-js "/js/sammy.js")
          (include-js "/js/app.js")
          [:title title]]
        [:body
          [:div#header
            [:h1#logo "Coards"]
            [:div#nav-container
              (render-login-link request)
              [:ul#nav
                [:li.first
                  (link-to (boards-url) "Boards")]
                [:li
                  (link-to "http://github.com/Abica/coards" "Source Code")]]
              [:div.clear]]]
          [:div#content
             body]
          [:div#footer
            "I'm implemented with "
            (link-to "http://clojure.org" "Clojure")
            " and "
            (link-to "http://compojure.com" "Compojure")
            [:p
              "Brought to you by "
              (link-to "http://github.com/Abica" "Abica")]]]))))
