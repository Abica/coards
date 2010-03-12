(ns coards.models
  (:import
    (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query Query$FilterOperator KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

(defn find-board [id]
  (get-entity (key-for "Board" id)))

(defn find-boards []
  (find-all (doto (Query. "Board") (.addSort "date"))))

(defn find-posts []
  (find-all (doto (Query. "Post") (.addSort "date"))))

(defn posts-for [parent]
 (let [posts (find-all (doto (Query. "Post" (:key parent))
                             (.addSort "date")))]
   (filter #(= (:key parent) (.getParent (:key %)))
           posts)))

(defn except [parent posts]
  (filter #(not= (:key parent) (:key %))
          posts))

(defn max-depth [depth posts]
  (filter #(-> %
             (partial split-at "/")
             count
             (partial > depth))
           posts))

;FIXME: figure out how to use PM.getObjectById
(defn find-post [post-id]
  (let [posts (find-posts)]
    (first
      (filter #(= (Long/valueOf post-id) (:id %))
                   posts))))

(defn do-create-board [user title message]
  (create {:kind "Board" :author user :title title :message message :date (java.util.Date.)}))

(defn do-create-post [user parent title message]
  (create {:kind "Post" :author user :title title :message message :date (java.util.Date.)}
          (:key parent)))
