(ns coards.models
  (:import
    (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

(defn find-board [id]
  (get-entity (key-for "Board" id)))

(defn find-boards []
  (find-all (doto (Query. "Board") (.addSort "date"))))

(defn find-posts []
  (find-all (doto (Query. "Post") (.addSort "date"))))

(defn posts-for [parent]
  (find-all (doto (Query. "Post" (:key parent))
                  (.addSort "date"))))

;TODO: figure out why FilterOperatpr.EQUAL won't work so this can be replaced with something more efficient,
;      since this sucks; without a way to add where clauses I have no choice but to pull all top level children
;      instead of grabbing just the post I want
;
;      it's probably got something to do with the sdk being old but for now I have no idea
(defn find-post [post-id]
  (let [posts (find-posts)]
; (let [ posts (find-all (doto (Query. "Post" (:key parent))
;                   ;(.addFilter "id" (com/google/appengine/api/datastore/Query/FilterOperator/EQUAL) post-id)
;                   (.addSort "date")))]
     (first
       (filter #(= (Integer. post-id) (:id %))
                    posts))))

(defn do-create-board [user title message]
  (create {:kind "Board" :author user :title title :message message :date (java.util.Date.)}))

(defn do-create-post [user parent title message]
  (create {:kind "Post" :author user :title title :message message :date (java.util.Date.)}
          (key-for parent)))
