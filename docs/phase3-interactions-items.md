# Phase 3: Interactions & Items

**Goal:** Doors, stairs that go both ways, pickups, inventory, basic HUD.

## New Files

### `Item.kt`
- `ItemType` enum: `KEY`, `HEALTH_POTION`, `SWORD`
- `Item` data class with type and grid position

### `Inventory.kt`
- Simple list-based container
- Methods: `add(item)`, `has(type)`, `remove(type)`

### `GameState.kt`
- Singleton holding:
  - `Inventory` instance
  - Current level number
  - Dungeon cache (`Map<Int, Pair<GameMap, List<Room>>>`) for backtracking between levels
  - Player stats (HP, attack, defense)

### `Hud.kt`
- Draws inventory icons at top of screen using tile sprites via `drawScreenTile`
- Shows current level number
- Uses fixed screen-space positioning (ignores camera)

## Modified Files

### `DungeonTileset.kt`
- Add item tile definitions with atlas coordinates:
  - `key` (pickup sprite)
  - `potion` (health potion sprite)
  - `sword` (weapon sprite)

### `SimpleTileRenderer.kt`
- `drawScreenTile(screenX, screenY, tileDef)` — renders at fixed screen position ignoring camera offset (already added in Phase 1 implementation)

### `DungeonGenerator.kt`
- Place locked doors at corridor chokepoints (single-tile-wide corridor entrances)
- Place a key in a reachable room before the locked door
- Scatter keys and potions in random rooms

### `DungeonScene.kt`
- **Door logic:** Closed doors block movement unless player has a KEY in inventory. On use, KEY is consumed and tile swaps to `doorOpen`
- **Stairs both ways:** Stairs-up on level > 1 returns to the cached previous level (restoring map and player position). Stairs-down advances to next level (generating or restoring from cache)
- **Item pickup:** Walking over an item tile auto-collects it into inventory
- **HUD rendering:** Call `Hud.render()` after map rendering

## Behavior

1. Player walks over a key tile → key added to inventory, tile becomes floor
2. Player walks into a closed door without a key → movement blocked
3. Player walks into a closed door with a key → key consumed, door opens, player moves through
4. Player walks onto stairs-down → scene switches to next level (generated or cached)
5. Player walks onto stairs-up (level > 1) → scene switches to previous cached level
6. HUD at top of screen shows inventory icons and "Level N"

## Verification

Run `./gradlew run` and confirm:
- Keys visible on floor in dungeon rooms
- Picking up a key shows it in HUD
- Locked doors block without key, open with key (key consumed)
- Stairs go both ways between cached levels
- HUD visible showing inventory and level number
