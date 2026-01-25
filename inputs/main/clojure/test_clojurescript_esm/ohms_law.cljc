(ns test-clojurescript-esm.ohms-law
  (:require
    #?(:cljs
       [cljs.spec.alpha :as spec :include-macros true]
       :default
       [clojure.spec.alpha :as spec])
    [emmy.generic :as g]
    [emmy.numbers]
    [test-clojurescript-esm.current :as current]
    [test-clojurescript-esm.resistance :as resistance]
    [test-clojurescript-esm.voltage :as voltage]))

; I = V/R
(spec/fdef I
  :args (spec/cat
          :keys
          (spec/keys* :req-un [::resistance/R ::voltage/V]))
  :ret ::current/I)

(defn I
  [& {:keys [R V]}]
  (g// V R))

#?(:cljs
   (defn ^:export I-js
     [opts]
     (I (js->clj opts :keywordize-keys true))))

; R = V/I
(spec/fdef R
  :args (spec/cat
          :keys
          (spec/keys* :req-un [::current/I ::voltage/V]))
  :ret ::resistance/R)

(defn R
  [& {:keys [I V]}]
  (g// V I))

#?(:cljs
   (defn ^:export R-js
     [opts]
     (R (js->clj opts :keywordize-keys true))))

; V = IR
(spec/fdef V
  :args (spec/cat
          :keys
          (spec/keys* :req-un [::current/I ::resistance/R]))
  :ret ::voltage/V)

(defn V
  [& {:keys [I R]}]
  (g/* I R))

#?(:cljs
   (defn ^:export V-js
     [opts]
     (V (js->clj opts :keywordize-keys true))))
