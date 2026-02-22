# Test ClojureScript + ESM

Test ESM in ClojureScript with advanced compilation. This is for mixed CLJS + JavaScript/TypeScript libraries where:

1. CLJS namespaces are compiled to ESM in `outputs/main/cljs`.
2. A `package.json` is added to `outputs/main/cljs` to turn it into a Node.js package named `clojure`.
	- npm and npm registries reject package names starting with `.` or `_` (e.g. `_clojure`) unless prefixed with a scope (e.g. `@scope/_clojure`).
		- Caveat: Bun accepts package names starting with `_`. This is preferred to prevent sniping.
	- Node.js rejects modules starting with `.`.
3. The local `clojure` Node.js package is added to `devDependencies` in the root `package.json`.
	- `devDependencies` aren't propagated to consumers. Consumers must generate their own local `clojure` Node.js package with the compiled CLJS namespaces.
4. JavaScript code in `inputs/main/javascript` imports compiled CLJS namespaces with `import { symbol } from "clojure/{namespace}.js"`.

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
├── package-lock.json 🤖
│
│   # Build recipes.
└── bb.edn
```

## Tools

- Babashka
- Java
- Clojure
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
# Node.js.
node inputs/main/javascript/main.js
```

## Notes

### Advanced Optimization Accidentally Removes "Unused" Imports Providing Specs

Advanced optimizations remove "unused" imports despite the imported namespaces registering specs. In this test project, the current + voltage + resistance namespaces only provide specs which are used by the Ohm's law namespace.

Compiled CLJS executes fine with unoptimized builds (change `bb.edn` from `(clojure "-M:cljs release main")` → `(clojure "-M:cljs compile main")`):

`node inputs/main/javascript/main.js`

```text
4
3
10
```

It complains about missing specs with advanced optimization:

`node inputs/main/javascript/main.js`

```text
file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.spec.alpha.js:14
vm=function(a){var b=um(a);if($APP.m(b))return b;if($APP.Wd(a))throw Error("Unable to resolve spec: "+$APP.X.g(a));return null};wm=function(a){if($APP.Ja(a==null?"":String(a)))return null;a=$APP.th.h($APP.Fl,$APP.Ll(a,"$"));if(2<=$APP.z(a)&&$APP.ze(function(c){return!$APP.Ja(c==null?"":String(c))},a)){var b=$APP.Dl($APP.El,$APP.tl)(a);a=$APP.D(b,0,null);b=$APP.D(b,1,null);return $APP.Q.g(""+$APP.X.g($APP.Kl(".",a))+"/"+$APP.X.g(b))}return null};$APP.ym=function(a){return $APP.Td($APP.xm,a)};
                                                                     ^

Error: Unable to resolve spec: :test-clojurescript-esm.current/I
    at vm (file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.spec.alpha.js:14:70)
    at Pm (file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.spec.alpha.js:19:106)
    at $APP.Im (file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.spec.alpha.js:18:429)
    at file:///workplace/test-clojurescript-esm/outputs/main/cljs/test-clojurescript-esm.ohms-law.js:26:190
    at ModuleJob.run (node:internal/modules/esm/module_job:343:25)
    at async onImport.tracePromise.__proto__ (node:internal/modules/esm/loader:665:26)
    at async asyncRunEntryPointWithESMLoader (node:internal/modules/run_main:117:5)

Node.js v22.21.1
```
