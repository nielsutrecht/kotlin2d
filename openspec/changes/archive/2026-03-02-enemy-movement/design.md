## Context

Enemies are static data classes placed by `DungeonGenerator` into rooms. They have position, type, and stats but no behavior. The player moves on a grid with a 0.15s cooldown timer. The game uses a shared mutable list of enemies per level, cached in `GameState`.

## Goals / Non-Goals

**Goals:**
- Turn-based enemy movement triggered by player steps
- Two-state AI: idle wandering and player-chasing
- Per-type behavior profiles (speed, detection range, wander style)
- Shared BFS pathfinding for efficient chase routing
- Contact = enemy dies + coin drop (placeholder for combat)

**Non-Goals:**
- Combat system (HP, damage, defense) — future work
- Animated movement transitions — enemies teleport tile-to-tile like the player
- Enemy spawning/respawning at runtime — only at generation time
- A* or other advanced pathfinding — BFS is sufficient for 80×60 maps

## Decisions

### Turn-based timing: enemies move after each player step
**Rationale:** The player already moves on a cooldown. Tying enemy turns to player steps keeps the pace predictable and avoids enemies acting while the player is idle. Each successful player move triggers one enemy update pass.
**Alternative:** Independent real-time timers per enemy. Rejected — adds complexity and feels chaotic with grid movement.

### Shared BFS distance map from player position
**Rationale:** One BFS per turn (O(width×height) = 4800 cells) gives every chasing enemy an optimal path. Each enemy just steps toward the neighbor with the lowest distance. Enemies and walls are impassable in the BFS, which naturally prevents stacking.
**Alternative:** Per-enemy BFS. Wasteful when multiple enemies chase the same target.

### Line-of-sight via grid raycast for detection
**Rationale:** Simple Bresenham-style walk from enemy to player. If any wall tile is hit, no LOS. Combined with a per-type range check, this determines when enemies switch from IDLE to CHASING.
**Alternative:** Room-based detection (enemy chases if player is in same room). Too coarse — misses corridor encounters.

### Enemy state stored on the data class
Add `state: EnemyState` (IDLE/CHASING), `spawnRoom: Room`, and `turnSkip: Int` fields directly to `Enemy`. Keeps things simple — no separate AI controller objects.

### Spawn room passed at generation time
`DungeonGenerator` already iterates rooms when placing enemies. Pass the `Room` reference so idle enemies can constrain wandering to their room bounds.

## Risks / Trade-offs

- [Enemies blocking corridors] → Single-tile corridors can become impassable if an enemy stands in one. Acceptable for now — player contact kills the enemy, clearing the path.
- [BFS every player step] → 4800-cell BFS is negligible but runs even when no enemies are chasing. Could gate on "any enemy is CHASING" but premature optimization.
- [Loss of LOS = immediate idle] → Enemies won't remember last known position. Could feel forgetful. Simple to extend later if needed.
