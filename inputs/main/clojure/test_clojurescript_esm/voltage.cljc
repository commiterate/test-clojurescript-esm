(ns test-clojurescript-esm.voltage
  (:require
    #?(:cljs
       [cljs.spec.alpha :as spec :include-macros true]
       :default
       [clojure.spec.alpha :as spec])
    [emmy.value :as v]))

(spec/def ::V
  v/number?)
