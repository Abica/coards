(ns coards.datastore-utils
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:import (com.google.appengine.api.datastore DatastoreServiceFactory Entity Key Query KeyFactory))
  (:use (coards layout utils url-helpers)
        (appengine-clj datastore users)))

; from http://www.fatvat.co.uk/2009/05/data-persistence-in-gae-with-clojure.html
(defn store
  [entity-type data]
  (let [entity (Entity. (.toString entity-type))]
    (doseq [[k v] data]
            (.setProperty entity (.toString k) v))
    (.put (DatastoreServiceFactory/getDatastoreService) entity)
    (.getKey entity)))

; from http://www.fatvat.co.uk/2009/05/data-persistence-in-gae-with-clojure.html
;(defn entity-to-map
 ; [entity]
  ;(into (hash-map) (.getProperties entity)))

; from http://www.fatvat.co.uk/2009/05/data-persistence-in-gae-with-clojure.html
(defn getEntity
  [entity-type id]
  (let [k (KeyFactory/createKey (.toString entity-type) (Long/valueOf id))]
    (entity-to-map
      (.get (DatastoreServiceFactory/getDatastoreService) k))))

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
  (some #(and
            (= (Integer. id) (% :id))
            %)
        -all-boards))

(defn posts-for-board [board]
  (:posts board))

(defn find-post [board-id post-id]
  (let [board (find-board board-id)]
    (some #(and
              (= (Integer. post-id) (% :id))
              %)
          (posts-for-board board))))
