(ns metabase.driver.redis
  (:require [metabase.driver :as driver]
            [metabase.query-processor.store :as qp.store]
            [metabase.driver.redis.query-processor :as redis.qp]
            [metabase.query-processor.reducible :as qp.reducible]
            [taoensso.carmine :as car
             :refer               (wcar)]))

(driver/register! :redis)

(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn- get-connection-url
  [{:keys [host port username password database_id] :as details}]
  (str "redis://" (when username (str username ":" password "@")) host ":" port "/"))


(defmethod driver/supports? [:redis :basic-aggregations] [_ _] false)

(defmethod driver/can-connect? :redis
  [driver detail]
  (println (get-connection-url detail))
  (def server1-conn {:pool {} :spec {:uri (get-connection-url detail)}})
  (= (wcar* (car/ping) "PONG")))

(defmethod driver/describe-database :redis
  [_ _]
  {:tables
   #{{:name "database" :schema nil}}})

(defmethod driver/describe-table :redis
  [_ _ {table-name :name}]
  {:name   table-name
   :schema nil
   :fields #{{:name              "key",
              :database-type     "STRING",
              :base-type         :type/Text,
              :database-position 0}
             {:name              "value",
              :database-type     "STRING",
              :base-type         :type/Text,
              :database-position 1}}})

(defmethod driver/mbql->native :redis
  [_ {{source-table-id :source-table} :query, :as mbql-query}]
  (println "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa-----------------------mbql-query:" mbql-query) ; NOCOMMIT
  (:name (qp.store/table source-table-id))
  "select * from test")

;(defmethod reducible-query :my-driver
;  [_ query context respond]
;  (with-open [results (run-query! query)]
;    (respond
;     {:cols [{:name \"my_col \"}]}
;     (qp.reducible/reducible-rows (get-row results) (context/canceled-chan context)))))
;(defmethod driver/execute-reducible-query :redis
;  [driver query context respond]
;  (println "bbbbbbbbbbbbbbbbbbbbbbbdriver:" driver) ; NOCOMMIT
;  (println "query:" query) ; NOCOMMIT
;  (println "context:" context) ; NOCOMMIT
;  (println "+++++++++++++++++++++++++++++++++++++ query:" respond) ; NOCOMMIT
;  ({:rows    (partition 2 (range 20))
;    :columns (for [i (range 1 3)]
;               (format "col_%d" i))}))

(defn run-query!
  [query]
  (println "qqqqqqqqqqqqqqqqqq" query))

(defn get-row
  [results]
  (println "rrrrrrrrrrrrrrrrrrrrrrr" results)
  [["aaaaa" "sssssss"]])

(defmethod driver/execute-reducible-query :redis
  [_ query chans return-results]
  (with-open [results (run-query! query)]
    (return-results
     {:cols [{:name "key"} {:name "value"}]}
     ([["aaaa", "bbbb"]]))))
;
;(defn execute-reducible-query
;  "Execute a `query` using the provided `do-query` function, and return the results in the usual format."
;  [query respond]
;  (let [headers          (.getColumnHeaders response)
;        columns          (map header->column headers)
;        getters          (map header->getter-fn headers)]
;    (respond
;     {:cols (for [col columns]
;              (add-col-metadata query col))}
;     (for [row (.getRows response)]
;       (for [[data getter] (map vector row getters)]
;         (getter data))))))
;
;
;(defmethod driver/execute-reducible-query :googleanalytics
;  [_ query _ respond]
;  (execute-reducible-query query respond))

