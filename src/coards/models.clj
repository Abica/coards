(ns coards.models
  (:import
    (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query Query$FilterOperator KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

(defn format-date
  "takes a map with a date key and formats it like 11/4/03 8:14 PM"
  [entity-map]
  (.. java.text.DateFormat
      (getInstance)
      (format (:date entity-map))))

(defn except
  "return a version of posts without parent"
  [parent posts]
  (filter #(not= (:key parent) (:key %))
          posts))

(defn max-depth [depth posts]
  (filter #(-> %
             (partial split-at "/")
             count
             (partial > depth))
           posts))

(defn find-object
  "take a base 64 encoded key and lookup it's entity"
  [encoded-key]
  (-> encoded-key get-key get-entity))

(defn find-boards
  "get a list of all boards"
  []
  (find-all (doto (Query. "Board") (.addSort "date"))))

(defn find-posts
  "get a list of all posts"
  []
  (find-all (doto (Query. "Post") (.addSort "date"))))

(defn posts-for
  "find all posts below parent"
  [parent]
  (let [posts (find-all (doto (Query. "Post" (:key parent))
                              (.addSort "date")))]
    (filter #(= (:key parent) (.getParent (:key %)))
            posts)))

(defn do-create-board
  [user title message]
  (create {:kind "Board"
           :author user
           :title title
           :message message
           :date (java.util.Date.)}))

(defn do-create-post
  [user parent title message]
  (create {:kind "Post"
           :author user
           :title title
           :message message
           :date (java.util.Date.)}
          (:key parent)))

(defn do-edit-post
  [encoded-key params]
  (edit {:key (get-key encoded-key)
         :title (:title params)
         :message (:message params)
         :updated-date (java.util.Date.)}))

(defn do-delete-object
  [encoded-key]
  (-> encoded-key get-key delete))
