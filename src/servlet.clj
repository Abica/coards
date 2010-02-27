(ns servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:import (com.google.appengine.api.datastore Query))
  (:use (compojure.html gen page-helpers form-helpers)
        compojure.http
        (compojure control)
        (coards layout utils url-helpers)))

(defn list-boards []
  (layout "Message boards"
    [:div#boards
      (map
        (fn [f]
              [:div
                (link-to (board-url f)
                         (str "Board id " f))])
        (range 1 20))]))

(defn view-board [id]
  (layout "Viewing board"
    [:h1 (str "Viewing board " id)]))

(defn create-board []
  (layout "Creating new board"
    [:div "CREATED"]))

(defn view-post [id]
  (layout "View post"
    [:h1 (str "Viewing post " id)]))

(defn create-post [board-id]
  (layout "Message boards"
    [:h1 (str "Message board " board-id)]))

(defn create-reply-to-post [post-id]
  (layout "Message boards"
    [:h1 (str "FORM HERE " post-id)]))

(defn list-replies-for [post-id])
(defn home [] (html [:h1 "Just you wait and see.."]))

(defroutes coards-app
  (GET  (boards-url)                (list-boards))
  (GET  (board-url :id)             (view-board  (:id params)))
  (POST (create-post-url :board-id) (create-post (:board-id params)))
  (GET  (post-url :id)              (view-post   (:id params)))
  (POST (create-board-url)          (create-board))
  (GET "/"                          (home))

  ; static files and error handling
  (GET "/*" (or (serve-file (params :*)) :next))
  (ANY "*"  (page-not-found)))

;(decorate static-routes short-circuit)

(defservice coards-app)
