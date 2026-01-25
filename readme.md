# Test ClojureScript + ESM

Test ESM in ClojureScript with advanced compilation. This is for mixed CLJS + JavaScript/TypeScript libraries where:

1. CLJS namespaces are compiled to ESM in `outputs/main/cljs`.
2. A `package.json` is added to `outputs/main/cljs` to turn it into a Node.js package named `_clojure`.
	- npm registries reject package names starting with `.` or `_` (e.g. `_clojure`) unless prefixed with a scope (e.g. `@scope/_clojure`).
	- Node.js rejects modules starting with `.`.
3. The local `_clojure` Node.js package is added to `devDependencies` in the root `package.json`.
	- `devDependencies` aren't propagated to consumers. Consumers must generate their own local `_clojure` Node.js package with the compiled CLJS namespaces.
4. JavaScript code in `inputs/main/javascript` imports compiled CLJS namespaces with `import { symbol } from "_clojure/{namespace}.js"`.

## Layout

```text
Key:
🤖 = Generated

.
│   # Build inputs.
├── inputs/
│   └── main/
│       │   # ClojureScript source.
│       ├── clojure/
│       │   └── test_clojurescript_esm/
│       │       ├── current.cljc
│       │       ├── resistance.cljc
│       │       ├── voltage.cljc
│       │       └── ohms-law.cljc
│       │
│       │   # JavaScript source.
│       └── javascript/
│           └── main.js
│
│   # Build outputs.
├── outputs/ 🤖
│   └── main/
│       │   # Compiled ClojureScript.
│       └── cljs/
│           ├── test-clojurescript-esm.current.js
│           ├── test-clojurescript-esm.resistance.js
│           ├── test-clojurescript-esm.voltage.js
│           ├── test-clojurescript-esm.ohms-law.js
│           └── package.json
│
│   # Reproducible shell configuration.
├── flake.nix
├── flake.lock 🤖
│
│   # Clojure path configuration.
├── deps.edn
│
│   # JavaScript path configuration.
├── package.json 🤖
├── bun.lock 🤖
│
│   # Build recipes.
└── bb.edn
```

## Tools

- Babashka
- treefmt
- Java
- Clojure
- cljfmt
- clj-kondo
- Bun
- Node.js

A reproducible shell can be created with [Nix](https://nixos.org) (described by the `flake.nix` + `flake.lock` files).

Nix can be installed with the [Determinate Nix Installer](https://github.com/DeterminateSystems/nix-installer) ([guide](https://zero-to-nix.com/start/install)).

Afterwards, you can change into the project directory and create the reproducible shell with `nix develop`.

You can also install the [direnv](https://direnv.net) shell extension to automatically load and unload the reproducible shell when you enter and leave the project directory.

Unlike `nix develop` which drops you in a nested Bash shell, direnv extracts the environment variables from the nested Bash shell into your current shell (e.g. Bash, Zsh, Fish).

## Developing

To build the project, run:

```shell
bb cljs
```

To run the Node.js application, run:

```shell
# Bun.
bun inputs/main/javascript/main.js

# Node.js.
node inputs/main/javascript/main.js
```

## Notes

### Advanced Optimization Accidentally Removes "Unused" Imports Providing Specs

Advanced optimizations remove "unused" imports despite the imported namespaces registering specs. In this test project, the current + voltage + resistance namespaces only provide specs which are used by the Ohm's law namespace.

Compiled CLJS executes fine with unoptimized builds (change `bb.edn` from `(clojure "-M:cljs release main")` → `(clojure "-M:cljs compile main")`):

`bun inputs/main/javascript/main.js`/`node inputs/main/javascript/main.js`

```text
4
3
10
```

It complains about missing specs with advanced optimization:

`bun inputs/main/javascript/main.js`

```text
 9 | gm=function(a,b,c){if($APP.Nd(c)){var d=$APP.oe($APP.Nl,$APP.th.h(a,c));return b.g?b.g(d):b.call(null,d)}return $APP.Ve(c)?(d=new $APP.sf(function(){var e=$APP.tb(c);return a.g?a.g(e):a.call(null,e)}(),function(){var e=$APP.ub(c);return a.g?a.g(e):a.call(null,e)}()),b.g?b.g(d):b.call(null,d)):$APP.kd(c)?(d=$APP.rl($APP.th.h(a,c)),b.g?b.g(d):b.call(null,d)):$APP.fd(c)?(d=$APP.yd(function(e,f){return $APP.qh.h(e,a.g?a.g(f):a.call(null,f))},c,c),b.g?b.g(d):b.call(null,d)):$APP.cj(c)?(d=$APP.Af.h($APP.am(c),
10 | $APP.th.h(a,c)),b.g?b.g(d):b.call(null,d)):b.g?b.g(c):b.call(null,c)};hm=function(a,b){if(a!=null&&a.Gb!=null)a=a.Gb(a,b);else{var c=hm[$APP.Ga(a==null?null:a)];if(c!=null)a=c.h?c.h(a,b):c.call(null,a,b);else if(c=hm._,c!=null)a=c.h?c.h(a,b):c.call(null,a,b);else throw $APP.Za("Spec.conform*",a);}return a};
11 | im=function(a,b,c,d){if(a!=null&&a.Hb!=null)a=a.Hb(a,b,c,d);else{var e=im[$APP.Ga(a==null?null:a)];if(e!=null)a=e.B?e.B(a,b,c,d):e.call(null,a,b,c,d);else if(e=im._,e!=null)a=e.B?e.B(a,b,c,d):e.call(null,a,b,c,d);else throw $APP.Za("Spec.gen*",a);}return a};jm=function(a,b){if(a!=null&&a.dc!=null)a=a.dc(a,b);else{var c=jm[$APP.Ga(a==null?null:a)];if(c!=null)a=c.h?c.h(a,b):c.call(null,a,b);else if(c=jm._,c!=null)a=c.h?c.h(a,b):c.call(null,a,b);else throw $APP.Za("Spec.with-gen*",a);}return a};
12 | lm=function(a){if($APP.Wd(a)){var b=$APP.n($APP.km);a=$APP.F(b,a);if($APP.Wd(a))a:{for(;;)if($APP.Wd(a))a=$APP.F(b,a);else{b=a;break a}b=void 0}else b=a;return b}return a};mm=function(a){if($APP.Wd(a)){var b=lm(a);if($APP.m(b))return b;throw Error("Unable to resolve spec: "+$APP.X.g(a));}return a};nm=function(a){return a!=null&&$APP.q===a.wc?a:null};pm=function(a){var b=om.g(a);return $APP.m(b)?a:b};
13 | rm=function(a,b){return $APP.Wd(a)?a:$APP.m(pm(a))?$APP.U.j(a,qm,b):a!=null&&(a.o&131072||$APP.q===a.Oc)?$APP.Zc(a,$APP.U.j($APP.$c(a),qm,b)):null};sm=function(a){return $APP.Wd(a)?a:$APP.m(pm(a))?qm.g(a):a!=null&&(a.o&131072||$APP.q===a.Oc)?qm.g($APP.$c(a)):null};um=function(a){var b=function(){var c=(c=$APP.Wd(a))?lm(a):c;if($APP.m(c))return c;c=nm(a);if($APP.m(c))return c;c=pm(a);return $APP.m(c)?c:null}();return $APP.m(pm(b))?rm(tm(b,null),sm(b)):b};
14 | vm=function(a){var b=um(a);if($APP.m(b))return b;if($APP.Wd(a))throw Error("Unable to resolve spec: "+$APP.X.g(a));return null};wm=function(a){if($APP.Ja(a==null?"":String(a)))return null;a=$APP.th.h($APP.Fl,$APP.Ll(a,"$"));if(2<=$APP.z(a)&&$APP.ze(function(c){return!$APP.Ja(c==null?"":String(c))},a)){var b=$APP.Dl($APP.El,$APP.tl)(a);a=$APP.D(b,0,null);b=$APP.D(b,1,null);return $APP.Q.g(""+$APP.X.g($APP.Kl(".",a))+"/"+$APP.X.g(b))}return null};$APP.ym=function(a){return $APP.Td($APP.xm,a)};
                                                                          ^
error: Unable to resolve spec: :test-clojurescript-esm.current/I
      at vm (/workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:14:70)
      at Pm (/workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:19:106)
      at /workplace/test-clojurescript-esm/node_modules/_clojure/test-clojurescript-esm.ohms-law.js:26:190
      at loadAndEvaluateModule (2:1)

Bun v1.3.2 (Linux x64)
```

`node inputs/main/javascript/main.js`

```text
file:///workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:14
vm=function(a){var b=um(a);if($APP.m(b))return b;if($APP.Wd(a))throw Error("Unable to resolve spec: "+$APP.X.g(a));return null};wm=function(a){if($APP.Ja(a==null?"":String(a)))return null;a=$APP.th.h($APP.Fl,$APP.Ll(a,"$"));if(2<=$APP.z(a)&&$APP.ze(function(c){return!$APP.Ja(c==null?"":String(c))},a)){var b=$APP.Dl($APP.El,$APP.tl)(a);a=$APP.D(b,0,null);b=$APP.D(b,1,null);return $APP.Q.g(""+$APP.X.g($APP.Kl(".",a))+"/"+$APP.X.g(b))}return null};$APP.ym=function(a){return $APP.Td($APP.xm,a)};
                                                                     ^

Error: Unable to resolve spec: :test-clojurescript-esm.current/I
    at vm (file:///workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:14:70)
    at Pm (file:///workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:19:106)
    at $APP.Im (file:///workplace/test-clojurescript-esm/node_modules/_clojure/cljs.spec.alpha.js:18:429)
    at file:///workplace/test-clojurescript-esm/node_modules/_clojure/test-clojurescript-esm.ohms-law.js:26:190
    at ModuleJob.run (node:internal/modules/esm/module_job:343:25)
    at async onImport.tracePromise.__proto__ (node:internal/modules/esm/loader:665:26)
    at async asyncRunEntryPointWithESMLoader (node:internal/modules/run_main:117:5)

Node.js v22.21.1
```
