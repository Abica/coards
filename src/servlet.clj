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
  (GET  (board-url :key)
        (view-board request (:key params)))

  ; view a post
  (GET  (post-url :key)
        (view-post request (:key params)))

  ; create a new board
  (POST (create-board-url)
        (create-board request params))

  ; create a new topic
  (POST (create-post-url :key)
        (create-post request params (:key params)))

  ; create a new reply
  (POST (create-reply-url :key)
        (create-post request params (:key params)))

  ; delete a post
  (GET (delete-post-url :key)
       (delete-post request params (:key params)))

  ; index
  (GET "/" (list-boards request))

  ; static files and error handling
  (GET "/*" (or (serve-file (params :*)) :next))
  (ANY "*"  (page-not-found)))

(defservice (wrap-with-user-info coards-app))
