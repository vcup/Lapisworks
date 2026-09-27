# 1. Dual-platform layout: Architectury Loom with Yarn mappings

Date: 2026-09-25
Status: Accepted

## Context

Lapisworks was Fabric-only: one Loom module, Yarn mappings, ~350 Java sources written
against Trinkets, Cardinal Components and roughly 40 distinct Fabric API touchpoints.
The goal is to also run natively on NeoForge 1.20.1 (NeoForge 47.1.106, published on
1.20.1 as `net.neoforged:forge`, keeping `net.minecraftforge.*` packages and the
`forge` mod id), without Sinytra Connector.

Three facts constrain the design:

- **Trinkets and Cardinal Components have no NeoForge build.** For 1.20.1 the native
  equivalents are Curios (`curios-forge`) and Forge Capabilities respectively. So
  `dev.emi.trinkets.api.*` and `dev.onyxstudios.cca.api.v3.*` cannot appear in shared
  code at all — a direct call is a guaranteed crash on the other loader.
- **Mappings cannot be changed cheaply.** All 350 sources are Yarn-named.
- **Hex Casting** — the addon's own dependency and the closest reference for this exact
  problem — solves it with a three-module layout whose `Common/` module is *loader-free*:
  it compiles against `-common`/`-xplat` artifacts in **Mojang official mappings**, uses
  `ServiceLoader` to resolve an `IXplatAbstractions` seam, and keeps all accessory code
  in per-platform `interop/trinkets` and `interop/curios` packages.

## Decision

Adopt a three-module Architectury Loom layout (`common/`, `fabric/`, `neoforge/`), but
**keep Yarn mappings** and compile `common` against each platform's own artifacts rather
than loader-free `-xplat` artifacts.

Adopt two ideas from Hex Casting verbatim:

1. **`ServiceLoader`-resolved seams** (`com.luxof.lapisworks.platform.Seams`) instead of a
   static `install()` singleton, so mixins and static initializers cannot race the platform
   entrypoint.
2. **A loader-agnostic accessory seam** (`Accessories` + `AccessorySlot`) modelled on Hex
   Casting's `DiscoveryHandlers`: each platform registers its own implementation, and shared
   code only ever sees the interface.

Reject the loader-free `Common/` variant, and adopt Architectury API as the
event/network/registry layer rather than hand-rolling one as Hex Casting does.

## Consequences

- Shared code cannot reference Fabric API, Trinkets or CCA; the module boundary enforces it.
- **Lapisworks gains a hard runtime dependency on Architectury API**, which it did not have
  before. Fabric users must install it. This is the main user-visible cost of the port.
- Because `common` compiles against Fabric artifacts under Yarn, the `-common`/`-xplat`
  ecosystem convention is unavailable; adding a new dependency means declaring it per platform.
- Mappings stay uniform, so the existing sources needed no rename pass.
- Accessory data is per platform: `data/trinkets/...` in `fabric/`, `data/curios/...` in
  `neoforge/`.
- Three platform differences cannot be hidden behind a single "register it and forget it" call and
  are worth knowing before touching init code:
  - **Registries are locked on NeoForge.** `Registry.register` only works during the loader's
    register event, so content initialization runs there and the seam queues writes for registries
    whose event has not fired yet. Hex Casting's own registries are *not* loader-managed and stay
    writable, which is why the seam has a separate `registerUnlocked`.
  - **Registering a `Block`/`Item` is itself a registry write** (they create an intrusive holder in
    their constructor), so content construction has to happen inside that same window.
  - **Client registration windows close early, and each closes at a different point.** The order
    measured on NeoForge is:
    ```
    mod constructor
    RegisterParticleProvidersEvent
    RegisterKeyMappingsEvent
    RegisterClientReloadListenersEvent
    FMLCommonSetupEvent
    FMLClientSetupEvent
    EntityRenderersEvent.AddLayers
    ```
    Shared client init is therefore split across three points: particle factories and key bindings
    from the constructor (the only point before their events), accessory renderers from
    `RegisterClientReloadListenersEvent`, and the rest from `FMLClientSetupEvent`. Two non-obvious
    constraints fix the middle one:
    - it must run **after** the item registry exists, because naming an item forces `ModItems`'
      static initializer, which constructs `Item`s and would otherwise throw
      "Registry is already frozen"; and
    - it must run **before** `AddLayers`, because Curios copies its pending renderer registry into
      its live one there (`CuriosRendererRegistry.load()`). Registering later leaves the entry
      pending forever and the worn accessories never draw.
  - **NeoForge has no builtin-item-renderer registry and no event for one.** An item whose model is
    `builtin/entity` must supply its renderer from `Item#initializeClient`, which Forge calls *from
    the `Item` constructor* and rejects if the extension is the item itself. So the renderer is
    attached by a NeoForge-only client mixin on the item class, and the actual renderer is looked up
    when the item is drawn rather than when it is registered.
  - **Forge loot tables do not support `conditions`.** Recipes do (Forge's `CraftingHelper` reads the
    key), but `LootTable`'s serializer does not, so a conditional loot table is parsed
    unconditionally and fails on any item it names that is absent. Data that is only valid when
    another mod is present therefore cannot live in `common/` for loot tables the way it can for
    recipes.
  - **A mixin targeting another mod's Minecraft-overriding method needs `remap = true`.** With a
    class-level `remap = false`, the name stays Yarn and the lookup fails on NeoForge because the
    Forge build of that mod names the override in SRG (`m_7378_`).
  - **Mixin configs that reach for a mod's classes must gate on that mod's version, not just its
    presence.** `MediaJarBlockEntityMixin` targets a class that only exists in hexical 2.0.0, so on
    1.5.0 a presence-only gate let Mixin look for a missing class and log a failure every startup.
    The sibling config already carried the `>= 2.0.0` bound; the mixin belongs in that bucket.

## Alternatives rejected

- **Loader-free `Common/` with Mojang mappings (Hex Casting's approach).** Would need every one
  of the 350 Yarn-named files rewritten to Mojang names, and the published `-common` artifacts
  do not exist in Yarn. Correct for a greenfield mod, prohibitively expensive as a retrofit.
- **Shim the Fabric APIs onto NeoForge** (hand-written `dev.emi.trinkets`/`net.fabricmc.fabric.api`
  compatibility layer). Achieves "no source changes" but reimplements a small Connector within
  the mod: large surface, fragile, and breaks whenever upstream APIs move.
- **Rely on Sinytra Connector** for the NeoForge side. Rejected by the project owner.
