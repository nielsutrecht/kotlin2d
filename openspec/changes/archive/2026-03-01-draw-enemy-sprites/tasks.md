## 1. Enemy Data Model

- [x] 1.1 Add `ENEMY` to the `TileKind` enum in `DungeonTileset.kt`
- [x] 1.2 Create `Enemy.kt` with `EnemyType` enum (GREEN_SLIME, SKELETON, BAT, RAT, GOBLIN with default stats) and `Enemy` data class (type, x, y, hp, attack, defense)
- [x] 1.3 Add enemy tile definitions (`greenSlime`, `skeleton`, `bat`, `rat`, `goblin`) to `DungeonTileset` with atlas coordinates from the sprite sheet

## 2. Enemy Spawning

- [x] 2.1 Add enemy spawning logic to `DungeonGenerator.generate()` — iterate rooms (skip first), place 1-3 enemies per room with ~60% chance, avoiding occupied tiles (walls, doors, stairs, items, other enemies)
- [x] 2.2 Implement level-scaled enemy type selection: GREEN_SLIME/RAT on level 1, +SKELETON/GOBLIN on level 2, +BAT on level 3+

## 3. Level Caching

- [x] 3.1 Add `enemies: MutableList<Enemy>` to `CachedLevel` in `GameState.kt`
- [x] 3.2 Update `DungeonScene` to restore enemies from cache on `onEnter()` and cache them on generation (matching existing item caching pattern)

## 4. Rendering

- [x] 4.1 Add enemy rendering in `DungeonScene.render()` — draw all enemies using `drawTileBatched()` after items and before the player sprite, using each enemy type's tile definition
