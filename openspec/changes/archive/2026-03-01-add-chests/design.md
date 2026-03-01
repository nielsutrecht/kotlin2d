## Context

The dungeon currently has two entity types besides tiles: `Item(type, x, y)` and `Enemy(type, x, y)`. Both are stored as mutable lists in `GeneratedDungeon` and cached per level in `CachedLevel`. Interactive tile-based objects only include doors (closed→open via tile swap). Chests are a new entity type that combines aspects of both: they have position and state like a tile object, but contain items and need to track open/closed state.

## Goals / Non-Goals

**Goals:**
- Add chests as entities that spawn in dungeon rooms with random contents
- Player opens chests by walking into them; items scatter onto adjacent floor tiles
- Chests render as sprites (closed/open) and persist across level transitions

**Non-Goals:**
- Locked chests requiring keys (future work)
- Chest loot tables varying by level or rarity
- Animated chest opening
- Inventory UI or chest inspection screen

## Decisions

**1. Chest as a standalone entity, not a new ItemType or tile**

`data class Chest(var x: Int, var y: Int, var opened: Boolean, val contents: List<ItemType>)`

Chests are fundamentally different from items (they block movement, have state, contain other items) and from tiles (they need per-instance state). A dedicated entity class stored in a list alongside items and enemies is the cleanest fit.

Alternative considered: tile-based like doors. Rejected — doors don't carry contents or per-instance data beyond open/closed, which is encoded in the tile itself. Chests need to track what's inside.

**2. Contents generated at spawn time**

When the generator places a chest, it immediately rolls 0-3 items (keys, potions, swords), max one of each type. Contents are stored on the `Chest` instance. This is simpler than generating at open time and allows the state to be cached.

**3. Items scatter onto adjacent floor tiles when opened**

On opening, the chest finds walkable floor tiles in the 4 cardinal directions (up/down/left/right). Items are placed on available floor tiles. If fewer floor tiles are available than items, excess items go directly to inventory as a fallback.

Alternative considered: straight to inventory. Rejected — scattering is more visual and rewarding, and the fallback handles edge cases.

**4. Chests block movement (opened or closed)**

The player walks *into* a chest to interact, but doesn't walk *onto* it. The movement check detects a chest at the target position and handles interaction instead of moving. This matches how doors work — interaction happens on attempted movement.

**5. Atlas coordinates: closed at (44,45), open at (45,45)**

Added as `chestClosed` and `chestOpen` TileDef entries with `TileKind.CHEST`.

## Risks / Trade-offs

- [Scatter blocked] All adjacent tiles could be walls/occupied → Mitigation: fallback to adding items directly to inventory
- [Chest on occupied tile] Generator could place a chest where an item or enemy already is → Mitigation: check `occupied` set during generation, same pattern as enemy spawning
