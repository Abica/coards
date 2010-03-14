(ns coards.utils)

(defn apply-str
  "takes a list of arguments and creates a string from them"
  [& xs]
  (apply str xs))

(defn split
  [str delim]
  (seq (.split str delim)))

(defn nl-to-br [str]
  (interpose [:br]
    (split str "\n")))
