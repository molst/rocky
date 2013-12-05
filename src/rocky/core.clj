(ns rocky.core
  (:require peridot.core)
  (:require [torpo.uri :as uri]))

(defn uri-to-peridot-request-args [uri]
  (let [path-str (apply uri/make-uri-path (:path uri))]
    (cons (if path-str path-str "/")
          (apply concat (seq (dissoc uri :path))))))

(defn get-uri-from-args [args] (let [fst (first args)] (when (map? fst) fst)))

(defn request
  "'args' can be either a seq of peridot args, including an uri string, OR a seq where the first element is a map representing a whole uri (including a :path sequence)."
  [routes & args]
  (apply (partial peridot.core/request (peridot.core/session routes))
         (if-let [uri (get-uri-from-args args)]
           (uri-to-peridot-request-args uri)
           args)))

(defn request-body [routes & args] (:body (:response (apply (partial request routes) args))))
(defn read-body [routes & args] (when-let [body (apply (partial request-body routes) args)] (read-string body)))
