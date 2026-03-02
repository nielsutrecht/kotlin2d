## 1. Enemy Data Model Updates

- [x] 1.1 Add `EnemyState` enum (IDLE, CHASING) to `Enemy.kt`
- [x] 1.2 Add `state`, `spawnRoom`, and `turnCounter` fields to `Enemy` data class
- [x] 1.3 Update `DungeonGenerator` to pass the spawn `Room` when creating enemies

## 2. Map Utilities

- [x] 2.1 Add BFS distance map method to `GameMap` that returns an `IntArray` with distances from a source tile, treating walls, closed doors, and a set of blocked positions as impassable
- [x] 2.2 Add line-of-sight raycast method to `GameMap` that walks a grid line between two points and returns false if any wall tile is hit

## 3. Enemy AI

- [x] 3.1 Create enemy update function that runs after each player step: iterate all enemies, check detection (LOS + range), update state, then move
- [x] 3.2 Implement idle behaviors: random wander (slime, rat), room wander (goblin), erratic movement (bat), patrol pacing (skeleton) — all constrained to spawn room
- [x] 3.3 Implement chase behavior: read from shared BFS distance map, step toward lowest-distance neighbor; bat uses 50/50 random vs toward-player
- [x] 3.4 Implement speed control: GREEN_SLIME skips every other turn via `turnCounter`

## 4. Contact Resolution

- [x] 4.1 Handle player-moves-onto-enemy: remove enemy, drop CoinPile(1-20), add gold, play sound
- [x] 4.2 Handle enemy-moves-onto-player: same removal + coin drop + sound during enemy update pass

## 5. Integration

- [x] 5.1 Wire enemy update into `DungeonScene.update()` — call after successful player movement
- [x] 5.2 Compute shared BFS distance map once per turn before enemy AI runs
- [x] 5.3 Verify enemies persist correctly in `GameState.dungeonCache` with new fields
