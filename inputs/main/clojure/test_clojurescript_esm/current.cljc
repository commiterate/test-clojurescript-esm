(ns test-clojurescript-esm.current
  (:require
    #?(:cljs
       [cljs.spec.alpha :as spec :include-macros true]
       :default
       [clojure.spec.alpha :as spec])
    [emmy.value :as v]))

(def I-spec
  (spec/def ::I
    v/number?))
