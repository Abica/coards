(ns servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (appengine-clj users)
        (coards controller utils url-helpers models)))

(defroutes coards-app
  (GET  (boards-url)                  (list-boards  request))
  (GET  (board-url :id)               (view-board   request (:id params)))
  (POST (create-post-url :board-id)   (create-post  request params (:board-id params)))
  (GET  (post-url :board-id :post-id) (view-post    request (:board-id params) (:post-id params)))
  (POST (create-board-url)            (create-board request params))
  (POST "/add-comment/:id"            (create-post  request params))
  (GET "/"                            (home))

  ; static files and error handling
  (GET "/*" (or (serve-file (params :*)) :next))
  (ANY "*"  (page-not-found)))

(defservice (wrap-with-user-info coards-app))
