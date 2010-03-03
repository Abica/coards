(ns coards.datastore-utils
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:import (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

(def -all-boards [
                  {:id 1 :title "Discussion" :desc "General purpose discussion"
                   :posts [
                           {:id 1
                            :title "Just a test"
                            :body "This is a test of the emergency broadcast system."
                            :author "Abica"}]}
                  {:id 2 :title "Games" :desc "Discuss all types of games"
                   :posts [
                           {:id 1 :title "I like to make games." :body "They are just amazing." :author "Abica"}]}
                  {:id 3 :title "Films" :desc "Chat with other movie buffs "
                   :posts []}
                  {:id 4 :title "Programming" :desc "Discuss programming"
                   :posts [
                           {:id 1 :title "Clojure is awesome!" :body "Clojure is super great." :author "Abica"}
                           {:id 2
                            :title "Compojure also rocks."
                            :body "Yeah, I also like compojure. It's cool."
                            :author "Abica"}
                           {:id 3
                            :title "It's amazing..."
                            :body "Just how far we've come since cgi scripts in C."
                            :author "Abica"}]}])

;;;;;;;;;;;;;;;;;;;;;;;;;;
; utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn find-board [id]
  (get-entity "Board" id))
;  (some #(and
 ;           (= (Integer. id) (% :id))
  ;          %)
   ;     -all-boards))

(defn find-boards []
  (find-all (doto (Query. "Board") (.addSort "date"))))

(defn posts-for-board [board]
  (.getChildren (key-for board)))

(defn find-post [board-id post-id]
  (let [board (find-board board-id)]
    (some #(and
              (= (Integer. post-id) (% :id))
              %)
          (posts-for-board board))))

(defn a-create-board [user title message]
  (create {:kind "Board" :author user :title title :message message :date (java.util.Date.)}))

(defn a-create-post [user parent title message]
  (create {:kind "Post" :author user :title title :message message :date (java.util.Date.)}
          (key-for parent)))
