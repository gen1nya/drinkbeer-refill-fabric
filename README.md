# DrinkBeer Refill — Fabric port

> **This is a modified fork.** It is an unofficial **Fabric 1.21.1** port of
> [DragonsPlusMinecraft/DrinkBeerRefill](https://github.com/DragonsPlusMinecraft/DrinkBeerRefill)
> (NeoForge, version `1.2.0`, commit `f3b2bfb`).
> The source has been changed relative to upstream: the loader layer was rewritten for Fabric,
> six upstream bugs were fixed, the JEI plugin was replaced with an EMI plugin.
>
> Everything that differs from upstream is listed in **[PORTING.md](PORTING.md)** (in Russian).
>
> Licensed under **AGPL-3.0-only**, same as upstream and as Lekavar's original DrinkBeer.

Requires Fabric Loader ≥ 0.19, Minecraft 1.21.1, Fabric API. Build with `./gradlew build`;
test the built jar on a real server with `./tools/prod-test.sh` (a dev `runServer` runs in
named mappings and cannot catch remapping bugs — see PORTING.md).

---

# DrinkBeer Refill (upstream readme)
This is an unofficial fork of **Drink Beer forge** mod, 
providing full fabric-3.0.2 version features in 1.19.2+ forge version and built-in JEI/Jade support, as well as other new features.

## Why refill?
Reasons are simple: 
* New ways to interact (e.g. Place Beer in Bartending Table and see it)
* Automation support by implementing Capability (e.g. Hopper supports Bartending Table)

## Credit
Thank following people for their amazing ground works!
* [Lekavar](https://github.com/Lekavar) and [MarbleGateKeeper](https://github.com/MarbleGateKeeper) made 1.17.1 and previous official versions.
* [Naetheline](https://github.com/Naetheline) made 1.18.1 version and ported `bartending` & `bartering` features.
* [yanang007](https://github.com/yanang007) made 1.18.2 version.
* [MarbleGateKeeper](https://github.com/MarbleGateKeeper) made JEI plugin.
* [yanang007](https://github.com/yanang007)  made Jade integration.
