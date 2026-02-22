// Emit compiled CLJS into a local `clojure` module.
import { I_js, R_js, V_js } from "clojure/test-clojurescript-esm.ohms-law.js";

console.log(I_js({V: 4, R: 1}));
console.log(R_js({I: 3, V: 9}));
console.log(V_js({I: 2, R: 5}));
