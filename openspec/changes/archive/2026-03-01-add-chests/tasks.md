## 1. Data Model & Tileset

- [x] 1.1 Create `Chest.kt` with `data class Chest(var x: Int, var y: Int, var opened: Boolean = false, val contents: List<ItemType>)`
- [x] 1.2 Add `TileKind.CHEST` to the enum in `DungeonTileset.kt`
- [x] 1.3 Add `chestClosed` (44,45) and `chestOpen` (45,45) tile definitions to `DungeonTileset`
- [x] 1.4 Add chests list to `GeneratedDungeon` and `CachedLevel`

## 2. Spawning

- [x] 2.1 Add chest spawning to `DungeonGenerator`: iterate rooms (skip first), ~20% chance, pick unoccupied floor tile, generate 0-3 random items (max one of each type)

## 3. Interaction & Rendering

- [x] 3.1 Add chest collision check in `DungeonScene.update()`: detect chest at target position, block movement, open if closed, play `dungeon/chest-open` sound
- [x] 3.2 Implement item scatter: find walkable cardinal neighbors, place items as entities, fallback excess to inventory
- [x] 3.3 Add chest rendering in `DungeonScene.render()`: draw closed/open sprite based on `opened` state

## 4. Persistence

- [x] 4.1 Wire chest list into `CachedLevel` save/restore in `DungeonScene.onEnter()` and `onExit()`
