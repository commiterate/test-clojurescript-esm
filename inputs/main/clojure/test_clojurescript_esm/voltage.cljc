(ns test-clojurescript-esm.voltage
  (:require
    #?(:cljs
       [cljs.spec.alpha :as spec :include-macros true]
       :default
       [clojure.spec.alpha :as spec])
    [emmy.value :as v]))

(def V-spec
  (spec/def ::V
    v/number?))
