#
# Nix flake.
#
# https://nix.dev/manual/nix/latest/command-ref/new-cli/nix3-flake#flake-format
# https://wiki.nixos.org/wiki/Flakes#Flake_schema
#
{
  # https://nix.dev/manual/nix/latest/command-ref/conf-file
  nixConfig = {
    flake-registry = "";
  };

  inputs = {
    # https://nixos.org/manual/nixpkgs/unstable
    # https://search.nixos.org/packages?channel=unstable
    nixpkgs = {
      type = "github";
      owner = "NixOS";
      repo = "nixpkgs";
      ref = "refs/heads/nixos-unstable";
    };
  };

  outputs =
    inputs:
    let
      # Output systems.
      #
      # https://github.com/NixOS/nixpkgs/blob/nixos-unstable/lib/systems/flake-systems.nix
      systems = [
        "aarch64-darwin"
        "aarch64-linux"
        "x86_64-darwin"
        "x86_64-linux"
      ];

      # Return an attribute set of system to the result of applying `f`.
      #
      # https://nixos.org/manual/nixpkgs/unstable#function-library-lib.attrsets.genAttrs
      genSystemAttrs = f: inputs.nixpkgs.lib.genAttrs systems f;
    in
    {
      # Development shells.
      #
      # For `nix develop` and direnv's `use flake`.
      devShells = genSystemAttrs (system: {
        # https://nixos.org/manual/nixpkgs/unstable#sec-pkgs-mkShell
        default = inputs.nixpkgs.legacyPackages.${system}.mkShell {
          packages = with inputs.nixpkgs.legacyPackages.${system}; [
            # Nix.
            #
            # Nix is dynamically linked on some systems. If we set LD_LIBRARY_PATH,
            # running Nix commands with the system-installed Nix may fail due to mismatched library versions.
            nix
            nixfmt
            # Utilities.
            coreutils
            # Git.
            git
            # Babashka.
            babashka
            # Treefmt.
            treefmt
            # Java.
            jdk21
            # Clojure.
            clojure
            cljfmt
            clj-kondo
            # JavaScript.
            bun
            nodejs
          ];

          shellHook = ''
            echo "⚗️"
          '';
        };
      });
    };
}
