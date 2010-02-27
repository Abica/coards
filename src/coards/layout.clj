(ns coards.layout
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use (compojure.html gen page-helpers form-helpers)
        (coards url-helpers)))

(defn navigation-links
  "takes a map in the form of {url text} and creates a navigation"
  [links]
  [:ul#nav
    (for [pair links]
      [:li (link-to (key pair) (val pair))])])

(defn layout
  [title & body]
  (html
    (doctype "xhtml/transitional")
    [:html
      [:head
        (include-css "/css/main.css")
        (include-js "/js/app.js")
        [:title title]]
      [:body
        (navigation-links {(boards-url)  "Boards"
                           (board-url 3) "A Board"
                           (post-url 5)  "Posts"})
        [:div#content
          [:h2
            [:a {:href "/"} "Home"]]]
                   body
        [:div#footer "Something"]]]))
