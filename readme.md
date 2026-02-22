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

### Advanced Optimization Accidentally Removes "Unused" Imports Providing Multimethod Implementations

Advanced optimizations remove "unused" imports despite the imported namespaces registering multimethod implementations. In this test project, the `emmy.numbers` namespace only provides multimethod implementations for `emmy.generic` multimethods which are used by the Ohm's law namespace.

Compiled CLJS executes fine with unoptimized builds (change `bb.edn` from `(clojure "-M:cljs release main")` → `(clojure "-M:cljs compile main")`):

`node inputs/main/javascript/main.js`

```text
4
3
10
```

It complains about missing multimethod implementations with advanced optimization:

`node inputs/main/javascript/main.js`

```text
file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.core.js:123
eh=function(a,b){throw Error(["No method in multimethod '",$APP.$a(a),"' for dispatch value: ",$APP.$a(b)].join(""));};$APP.gh=function(a,b,c,d,e,f,g){var h=$APP.fh;this.name=a;this.M=b;this.be=h;this.xc=c;this.Bc=d;this.ye=e;this.Ac=f;this.qc=g;this.o=4194305;this.K=4352};$APP.W=function(a,b,c){$APP.ah.B(a.Bc,$APP.U,b,c);bh(a.Ac,a.Bc,a.qc,a.xc)};
                       ^

Error: No method in multimethod 'emmy.generic/one?' for dispatch value: [:emmy.value/native-integral]
    at eh (file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.core.js:123:24)
    at $APP.l.g (file:///workplace/test-clojurescript-esm/outputs/main/cljs/cljs.core.js:372:449)
    at $APP.DG.h (file:///workplace/test-clojurescript-esm/outputs/main/cljs/emmy.generic.js:51:456)
    at I_js (file:///workplace/test-clojurescript-esm/outputs/main/cljs/test-clojurescript-esm.ohms-law.js:37:111)
    at file:///workplace/test-clojurescript-esm/inputs/main/javascript/main.js:4:13
    at ModuleJob.run (node:internal/modules/esm/module_job:343:25)
    at async onImport.tracePromise.__proto__ (node:internal/modules/esm/loader:665:26)
    at async asyncRunEntryPointWithESMLoader (node:internal/modules/run_main:117:5)

Node.js v22.21.1
```

Manually adding back `import "./emmy.numbers.js";` to `outputs/main/cljs/test-clojurescript-esm.ohms-law.js` fixes this.
