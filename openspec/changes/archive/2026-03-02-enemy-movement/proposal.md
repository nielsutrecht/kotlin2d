## Why

Enemies are currently static decorations — they spawn in rooms but never move or interact with the player. Making them move and react to the player brings the dungeon to life and creates the foundation for combat mechanics.

## What Changes

- Enemies gain a turn-based AI system: each time the player moves, all enemies take one step
- Two behavioral states: IDLE (wander within spawn area) and CHASING (pursue player indefinitely)
- Detection via line-of-sight raycast within a per-type detection range triggers chase
- Per-type behavior profiles: different wander styles, chase speeds, detection ranges
- Shared BFS pathfinding from player position for efficient chase routing
- Enemies cannot stack on each other, on the player, or on walls/doors
- Contact with player (either direction) kills the enemy and drops a CoinPile (1-20 gold) — placeholder for future combat

## Capabilities

### New Capabilities
- `enemy-movement`: Turn-based enemy AI with idle wandering, player detection, chase behavior, pathfinding, and contact resolution

### Modified Capabilities
- `enemy-sprites`: Enemy data model gains state tracking (idle/chasing), spawn room reference, and movement speed fields

## Impact

- `Enemy.kt` — new fields: AI state, spawn room, speed/skip counter
- `DungeonScene.kt` — enemy update step after player movement, contact handling
- `GameMap.kt` — BFS distance map utility, line-of-sight raycast
- `DungeonGenerator.kt` — pass spawn room to enemies
- New sound for enemy-contact event (or reuse existing coin sounds)
