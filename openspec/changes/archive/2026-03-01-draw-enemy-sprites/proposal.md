## Why

The dungeon currently has no enemies — the player can explore freely without encountering any threats. Adding enemy sprites is the visual foundation for a combat system: enemies need to be visible on the map before they can interact with the player.

## What Changes

- Define enemy sprite tile definitions in the tileset (e.g., skeleton, slime, bat) using existing atlas coordinates from `dungeon-tileset.png`
- Create an `Enemy` data class to represent enemies with position, type, and basic stats
- Spawn enemies in dungeon rooms during level generation
- Render enemy sprites on the dungeon map alongside items and the player
- Cache enemies per level (similar to items) so they persist when backtracking

## Capabilities

### New Capabilities
- `enemy-sprites`: Defines enemy types, spawning enemies in rooms, and rendering them on the dungeon map

### Modified Capabilities

## Impact

- `DungeonTileset.kt` — new enemy tile definitions
- New `Enemy.kt` file — enemy data class and type enum
- `DungeonGenerator.kt` — enemy spawning logic in rooms
- `DungeonScene.kt` — enemy rendering and per-level caching
- `GameState.kt` — enemy list in cached level data
