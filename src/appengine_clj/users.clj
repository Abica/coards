(ns appengine-clj.users
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)))

(defn get-user-info [request]
  (:appengine-clj/user-info request))

(defn user-info
  "With no arguments, returns a UserService and User for the current request in a map keyed by :user-service and :user respectively.
  If the user is not logged in, :user will be nil.
  With a single map argument, a Ring request, returns the user-info map associated with the request by wrap-with-user-info."
  ([]
   (let [user-service (UserServiceFactory/getUserService)]
     {:user (.getCurrentUser user-service) :user-service user-service}))
  ([request]
   (:appengine-clj/user-info request)))

(defn logged-in?
  "determine whether or not a user is logged in"
  ([]
    (logged-in? (user-info)))
  ([info] (:user info)))

(defn author?
  "determines whether the currently logged in user the author of post"
  [post]
  (= (:user (user-info))
     (:author post)))

(defn admin?
  "determine whether the currently logged in user is an admin account"
  []
  (let [info (user-info)]
    (if (logged-in? info)
      (-> info :user-service .isUserAdmin))))

(defn owner?
  "determine whether the current user has write/delete priviledges for post"
  [post]
  (or (author? post)
      (admin?)))

(defn wrap-with-user-info
  "Ring middleware method that wraps an application so that every request will have
  a user-info map assoc'd to the request under the key :appengine-clj/user-info."
  [application]
  (fn [request]
    (application (assoc request :appengine-clj/user-info (user-info)))))

(defn wrap-requiring-login
  ([application] (wrap-requiring-login application nil))
  ([application destination-uri]
    (let [uri-fn (if destination-uri
                   (fn [_] destination-uri)
                   (fn [request] (:uri request)))]
      (fn [request]
        (let [user-service (:user-service (user-info))]
          (if (.isUserLoggedIn user-service)
            (application request)
            {:status 302 :headers {"Location" (.createLoginURL user-service (uri-fn request))}}))))))
