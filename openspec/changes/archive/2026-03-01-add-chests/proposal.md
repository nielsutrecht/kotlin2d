## Why

The dungeon lacks interactive objects beyond doors and item pickups. Chests are a staple dungeon feature that adds exploration reward and surprise — the player doesn't know what's inside until they open one.

## What Changes

- New `Chest` entity with position, open/closed state, and generated contents
- Chest tile definitions in the tileset (closed at atlas 44,45 / open at 45,45)
- Chests spawn in the dungeon generator: max one per room, placed sparingly
- Player opens a chest by walking into it; contents scatter onto adjacent floor tiles
- Chest-open sound group (`dungeon/chest-open`) plays on open
- Chests persist in level cache for backtracking

## Capabilities

### New Capabilities
- `chests`: Chest entity, spawning, interaction, and rendering

### Modified Capabilities

None — the sfx-system and sound-groups specs don't need changes since the `dungeon/chest-open` sound group already exists and works via `Audio.playSound`.

## Impact

- `Chest.kt`: New entity file
- `DungeonTileset.kt`: Add `chestClosed` and `chestOpen` tile definitions, `TileKind.CHEST`
- `DungeonGenerator.kt`: Chest spawning logic with content generation
- `DungeonScene.kt`: Chest interaction (walk-into opens, scatter items) and rendering
- `GameState.kt` / `CachedLevel`: Add chest list for level persistence
