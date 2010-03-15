(ns appengine-clj.datastore
  (:import (com.google.appengine.api.datastore
            DatastoreServiceFactory Entity Key Query KeyFactory)))

(defn get-key
  ([map-or-str]
    (if (instance? String map-or-str)
      (KeyFactory/stringToKey map-or-str)
      (:key map-or-str)))
  ([kind id]
    (KeyFactory/createKey (.toString kind) (Long/valueOf id))))

(defn encode-key [key]
 (KeyFactory/keyToString key))

(defn entity-to-map
  "Converts an instance of com.google.appengine.api.datastore.Entity
  to a PersistentHashMap with properties stored under keyword keys,
  plus the entity's kind stored under :kind and key stored under :key."
  [#^Entity entity]
  (reduce #(assoc %1 (keyword (key %2)) (val %2))
    {:kind (.getKind entity)
     :key (.getKey entity)
     :encoded-key (encode-key (.getKey entity))
     :id (.getId (.getKey entity))}
    (.entrySet (.getProperties entity))))

(defn get-entities
  "Retrieves the identified entity or raises EntityNotFoundException."
  ([& #^Key keys]
    (let [entities (.get (DatastoreServiceFactory/getDatastoreService) keys)]
      (map entity-to-map (vals entities)))))

(defn get-entity
  "Retrieves the identified entity or raises EntityNotFoundException."
  ([kind id]
    (get-entity kind id))
  ([#^Key key]
   (first (get-entities key))))

(defn find-all
  "Executes the given com.google.appengine.api.datastore.Query
  and returns the results as a lazy sequence of items converted with entity-to-map."
  [#^Query query]
  (let [data-service (DatastoreServiceFactory/getDatastoreService)
        results (.asIterable (.prepare data-service query))]
    (map entity-to-map results)))

(defn create
  "Takes a map of keyword-value pairs and puts a new Entity in the Datastore.
  The map must include a :kind String.
  Returns the saved Entity converted with entity-to-map (which will include the assigned :key)."
  ([item] (create item nil))
  ([item #^Key parent-key]
    (let [kind (item :kind)
          properties (dissoc item :kind)
          entity (if parent-key (Entity. kind parent-key) (Entity. kind))]
      (doseq [[prop-name value] properties] (.setProperty entity (name prop-name) value))
      (.put (DatastoreServiceFactory/getDatastoreService) entity)
      (entity-to-map entity))))

(defn update
  "Takes a map of params and a key to an existing entity and updates it.
   Also adds an updated-date timestamp to the entity"
  [params #^Key key]
  (let [entity (.get (DatastoreServiceFactory/getDatastoreService) key)
        updated-params (assoc params :updated-date (java.util.Date.))]
    (doseq [[prop-name value] updated-params]
      (.setProperty entity (name prop-name) value))
    (.put (DatastoreServiceFactory/getDatastoreService) entity)
    (entity-to-map entity)))

(defn delete
  "Deletes the identified entities."
  [& #^Key keys]
  (.delete (DatastoreServiceFactory/getDatastoreService) keys))

