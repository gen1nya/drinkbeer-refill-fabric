# Drink Beer Rebrew

> **This is a modified fork.** It is an unofficial **Fabric 1.21.1** port of *Drink Beer Refill*
> (NeoForge), forked at commit
> [`f3b2bfb`](https://github.com/DragonsPlusMinecraft/DrinkBeerRefill/tree/f3b2bfb),
> which was published under
> [**AGPL-3.0-only**](https://github.com/DragonsPlusMinecraft/DrinkBeerRefill/blob/f3b2bfb/LICENSE).
> *Drink Beer Refill* is itself a fork of Lekavar's original *Drink Beer*.
>
> The source has been changed relative to upstream: the loader layer was rewritten for Fabric,
> eight upstream bugs were fixed, the JEI plugin was replaced with an EMI plugin.
> Everything that differs is listed in **[PORTING.md](PORTING.md)** (in Russian).

## Provenance and licence

Every link to upstream in this repository is **pinned to commit `f3b2bfb`** on purpose, because
the upstream repository has moved on since the fork:

- on 2026-08-07 upstream released `1.4.0-beta.1` and, in the same commit
  ([`8130c4c`](https://github.com/DragonsPlusMinecraft/DrinkBeerRefill/commit/8130c4c)),
  replaced the AGPL-3.0 licence text with **All Rights Reserved**;
- upstream also added its own Fabric support in that release, so an official Fabric build of
  *Drink Beer Refill* now exists — if you want the original mod rather than this fork, go there.

This fork descends from the AGPL-3.0 state of the project and carries **AGPL-3.0-only** forward,
as that licence requires. AGPL-3.0 §2 grants rights "for the term of copyright on the Program"
and states they are "irrevocable provided the stated conditions are met", so the later relicensing
applies to upstream's newer work, not retroactively to what was already published under AGPL.
**No code from upstream commits after `f3b2bfb` is used here**, and none will be taken while the
upstream licence stays as it is.

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
