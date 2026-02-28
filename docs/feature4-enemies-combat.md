# Feature: Enemies & Turn-Based Combat

## Summary

Add enemies that populate dungeon rooms and a roguelike turn-based combat system. Enemies act after the player moves. A message log provides combat feedback, and the HUD shows player health.

## Prerequisites

- Phase 3 (items, inventory, HUD)

## Requirements

### Entity Model

- `Entity` class shared by player and enemies.
- Fields: grid position (`x`, `y`), `TileDef`, `hp`, `maxHp`, `attack`, `defense`, `name`.
- `takeDamage(amount)` reduces HP by `max(1, amount - defense)`.
- `isAlive` property (`hp > 0`).

### Enemy AI

- **Chase:** if the player is within 10 tiles (Manhattan distance), move one step toward the player along the axis with the greater gap.
- **Attack:** if adjacent to the player, attack instead of moving.
- **Idle:** otherwise do nothing.
- Respects `GameMap.isWalkable` and does not overlap other enemies.

### Turn Flow (`DungeonScene.update`)

1. Read player input to determine a target cell.
2. If the target contains an enemy → player attacks it; log the result.
3. Otherwise if the target is walkable → move the player.
4. After the player acts, every living enemy takes one AI turn.
5. Remove dead enemies from the entity list.
6. If the player dies → game over (restart or dedicated scene).

### Enemy Spawning

- Generator places 1–3 enemies per room (starting room excluded).
- Types scale with dungeon depth:

| Levels | Pool |
|--------|------|
| 1–2 | Rat |
| 3–4 | Rat, Skeleton |
| 5+ | Rat, Skeleton, Demon |

### Combat Balancing

| Entity | HP | Attack | Defense |
|--------|----|--------|---------|
| Player | 20 | 5 | 2 |
| Rat | 5 | 2 | 0 |
| Skeleton | 10 | 4 | 1 |
| Demon | 18 | 7 | 3 |

Damage formula: `max(1, attacker.attack - defender.defense)`.

### Message Log

- `MessageLog` singleton stores combat/event strings.
- `add(message)` appends; `recent(n = 5)` returns the last N entries.
- Examples: *"You hit the Rat for 5 damage"*, *"Skeleton attacks you for 2 damage"*, *"You killed the Rat"*.

### HUD Extensions

- HP indicator (hearts or text).
- Message log displayed at the bottom of the screen (last 3–5 lines).

## File Changes

| File | Action | Detail |
|------|--------|--------|
| `Entity.kt` | Create | Entity class with stats and damage logic |
| `EnemyAi.kt` | Create | Per-enemy chase/attack/idle AI |
| `MessageLog.kt` | Create | Singleton combat message log |
| `DungeonTileset.kt` | Modify | Add `rat`, `skeleton`, `demon` tile defs |
| `DungeonGenerator.kt` | Modify | Spawn enemies per room, scaled by level |
| `DungeonScene.kt` | Modify | Turn-based loop, bump combat, entity rendering |
| `Hud.kt` | Modify | HP indicator, message log display |

## Acceptance Criteria

1. Enemies appear in dungeon rooms (not the starting room).
2. Bumping into an enemy triggers an attack with a damage message in the log.
3. After the player's turn, enemies move toward and attack the player.
4. Killed enemies are removed from the map.
5. The player can die, triggering a game-over state.
6. Health potions (from Phase 3) restore HP when consumed.
7. HP and the message log are visible in the HUD.

## Verification

```bash
./gradlew run
```

- Enemies visible in rooms, absent from starting room.
- Bump combat works both ways with log messages.
- Dead enemies disappear; player death triggers game over.
- HUD shows HP and recent combat messages.
