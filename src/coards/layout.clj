(ns coards.layout
  (:use (compojure.html gen page-helpers form-helpers)
        (appengine-clj users)
        (coards url-helpers)))

(defn navigation-links
  "takes a map in the form of {url text} and creates a navigation"
  [links]
  [:ul#nav
    (for [pair links]
      [:li (link-to (key pair) (val pair))])])

(defn breadcrumb-trail []
  "Boards -> Programming -> Yo..")

(defn render-login-link [request]
  (let [info (:appengine-clj/user-info request)
        user (:user info)
        user-service (:user-service info)]
    (if (logged-in? info)
       [:span.user "Logged in as " (.getNickname user) " ("
         (link-to (.createLogoutURL user-service (:uri request)) "logout") ")"]
       [:span.user (link-to (.createLoginURL user-service (:uri request)) "login to comment")])))

(defn layout
  [request title & body]
  (html
    (doctype "xhtml/transitional")
    [:html
      [:head
        (include-css "/css/main.css")
        (include-js "/js/app.js")
        [:title title]]
      [:body
        [:div#header
          [:h1#logo "Coards"]
          [:div#nav-container
            (render-login-link request)
            (navigation-links {(boards-url)   "Boards"
                               (board-url 3)  "The Film Board"
                               (post-url 4 5) "A Missing Post"})]
          [:div.clear]]
        [:div#content
          [:div#breadcrumb
            (breadcrumb-trail)]
           body]
        [:div#footer
          "I'm implemented with "
          (link-to "http://clojure.org" "Clojure")
          " and "
          (link-to "http://compojure.com" "Compojure")
          [:p
            "Brought to you by "
            (link-to "http://github.com/Abica" "Abica")]]]]))
