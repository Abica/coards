(ns coards.utils)

(defn apply-str
  "takes a list of arguments and creates a string from them"
  [& xs]
  (apply str xs))
