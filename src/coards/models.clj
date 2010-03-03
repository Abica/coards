(ns coards.models
  (:import (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

(defn find-board [id]
  (get-entity "Board" id))

(defn find-boards []
  (find-all (doto (Query. "Board") (.addSort "date"))))

(defn posts-for-board [board]
  (find-all (doto (Query. "Post" (:key board))
                  (.addSort "date"))))


(defn find-post [board-id post-id]
  (find-all (Query. "Post" "id = :1" post-id)))

(defn a-create-board [user title message]
  (create {:kind "Board" :author user :title title :message message :date (java.util.Date.)}))

(defn a-create-post [user parent title message]
  (create {:kind "Post" :author user :title title :message message :date (java.util.Date.)}
          (key-for parent)))
