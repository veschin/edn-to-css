(ns css
  #?(:cljs (:require-macros [css]))
  (:require [clojure.string :as string]))

(defn css->edn [css-string]
  (into
   {}
   (for [line (string/split css-string #";")
         :let [[key option] (map string/trim (string/split line #":"))]]
     {(keyword key)
      (if (re-find #"," option)
        (str option)
        (keyword option))})))

(defn full-css->edn [style-tag]
 (into
  {}
  (for [[elem opts] (partition-all 2 (clojure.string/split style-tag #"(\{[\s\S]+?\})"))
        :let [elem (clojure.string/replace elem #"\n" "")
              opts (clojure.string/replace opts #"[{|}|\n]" "")]]
    {elem (css->edn opts)})))

(defn ->fns [fn-map]
  (string/join
   " "
   (for [[fn- args] fn-map]
     (str (name fn-) "(" (string/join ", " (map name args)) ")"))))

(defn type-cast [value]
  (cond
    (keyword? value) (name value)
    (symbol? value) (str value)
    (map? value) (->fns value)
    (vector? value) (string/join ", " (map type-cast value))
    :num value))

(defn important? [prop value]
  (let [bang? (-> prop first #{"!"})
        prop (if bang? (subs prop 1) prop)]
    (str prop ": " value
         (when bang? " !important") ";")))

(defn prop<->value [[prop value]]
  (important? (type-cast prop) (type-cast value)))

(defn tag<->props [[tag props] & [nested-tag?]]
  (let [nested (:nested props)
        ->val (fn [fn- val] (string/join "\n " (map fn- val)))
        nested-tag? (when nested-tag? (str (type-cast nested-tag?) " "))
        ->tag (fn [val] (str nested-tag? (type-cast tag) " {" val "}"))]
    (if nested
      (string/join
       "\n"
       [(->tag (->val prop<->value (dissoc props :nested)))
        (->val #(tag<->props % tag) nested)])
      (->tag (->val prop<->value props)))))

(defmacro sub-let [let- & body]
  `(let ~(or let- []) ~@body))

(defmacro ->style [{let- :let
                    :as edn-style}]
  `(->> ~(dissoc edn-style :let)
        (map tag<->props)
        (sub-let ~let-)
        (string/join "\n")))

(defmacro style-tag [edn-style]
  `[:style (->style ~edn-style)])
