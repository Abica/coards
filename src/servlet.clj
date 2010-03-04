(ns servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj users)
        (coards controller utils url-helpers models)))

(defroutes coards-app
  ; view all boards
  (GET  (boards-url)
        (list-boards request))

  ; view a board
  (GET  (board-url :id)
        (view-board request (:id params)))

  ; view a post
  (GET  (post-url :id)
        (view-post request (:id params)))

  ; create a new board
  (POST (create-board-url)
        (create-board request params))

  ; create a new topic
  (POST (create-post-url :parent-id)
        (create-post request params find-board (:parent-id params)))

  ; create a new reply
  (POST (create-reply-url :parent-id)
        (create-post request params find-post (:parent-id params)))

  ; index
  (GET "/" (home))

  ; static files and error handling
  (GET "/*" (or (serve-file (params :*)) :next))
  (ANY "*"  (page-not-found)))

(defservice (wrap-with-user-info coards-app))
