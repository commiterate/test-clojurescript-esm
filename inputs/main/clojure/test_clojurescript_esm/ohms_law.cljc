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
(def I-fspec
  (spec/fdef I
    :args (spec/cat
            :keys
            (spec/keys* :req-un [resistance/R-spec voltage/V-spec]))
    :ret current/I-spec))

(defn I
  [& {:keys [R V]}]
  (g// V R))

#?(:cljs
   (defn ^:export I-js
     [opts]
     (I (js->clj opts :keywordize-keys true))))

; R = V/I
(def R-fspec
  (spec/fdef R
    :args (spec/cat
            :keys
            (spec/keys* :req-un [current/I-spec voltage/V-spec]))
    :ret resistance/R-spec))

(defn R
  [& {:keys [I V]}]
  (g// V I))

#?(:cljs
   (defn ^:export R-js
     [opts]
     (R (js->clj opts :keywordize-keys true))))

; V = IR
(def V-fspec
  (spec/fdef V
    :args (spec/cat
            :keys
            (spec/keys* :req-un [current/I-spec resistance/R-spec]))
    :ret voltage/V-spec))

(defn V
  [& {:keys [I R]}]
  (g/* I R))

#?(:cljs
   (defn ^:export V-js
     [opts]
     (V (js->clj opts :keywordize-keys true))))
