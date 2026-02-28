# Feature: Interactions & Items

## Summary

Add an item/inventory system, interactive doors and two-way stairs, and a HUD. The player can pick up keys and potions, unlock doors, and travel between cached dungeon levels.

## Prerequisites

- Phase 2 (procedural dungeons, scene switching)

## Requirements

### Items & Inventory

- `ItemType` enum with `KEY`, `HEALTH_POTION`, `SWORD`.
- `Item` data class holding a type and grid position.
- `Inventory` — list-based container with `add`, `has`, `remove`.

### Game State

- `GameState` singleton tracks:
  - Player inventory
  - Current level number
  - Dungeon cache (`Map<Int, Pair<GameMap, List<Room>>>`) so the player can backtrack
  - Player stats (HP, attack, defense) to carry across levels

### Door Logic

- A closed door blocks movement.
- If the player has a `KEY`, moving into a closed door consumes the key, swaps the tile to `doorOpen`, and the player walks through.
- The generator places locked doors at single-tile-wide corridor entrances and puts a key in a reachable room before the door.

### Stairs (Both Ways)

- Stairs-down advances to the next level (generates a new dungeon or restores from cache).
- Stairs-up on level > 1 returns to the cached previous level, restoring the player's prior position.

### Item Spawning

- The generator scatters keys and potions in random rooms.

### HUD

- Rendered in screen space (ignores camera) via `drawScreenTile`.
- Shows inventory icons and current level number at the top of the screen.

## File Changes

| File | Action | Detail |
|------|--------|--------|
| `Item.kt` | Create | `ItemType` enum, `Item` data class |
| `Inventory.kt` | Create | List-based inventory container |
| `GameState.kt` | Create | Singleton: inventory, level, dungeon cache, player stats |
| `Hud.kt` | Create | Screen-space HUD drawing inventory icons and level |
| `DungeonTileset.kt` | Modify | Add `key`, `potion`, `sword` tile definitions |
| `DungeonGenerator.kt` | Modify | Place doors, keys, and potions during generation |
| `DungeonScene.kt` | Modify | Door interaction, two-way stairs, item pickup, HUD rendering |

## Acceptance Criteria

1. Keys are visible on the floor in dungeon rooms.
2. Walking over a key adds it to the inventory and replaces the tile with floor.
3. A closed door blocks movement when the player has no key.
4. A closed door opens (and consumes a key) when the player has one.
5. Stairs-down switches to the next level.
6. Stairs-up (level > 1) returns to the cached previous level.
7. The HUD displays inventory icons and "Level N" at the top of the screen.

## Verification

```bash
./gradlew run
```

- Keys on floor, pickable, shown in HUD.
- Locked doors block without key, open with key (key consumed).
- Stairs navigate both directions between cached levels.
- HUD visible at all times.
