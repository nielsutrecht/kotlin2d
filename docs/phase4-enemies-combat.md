# Phase 4: Enemies & Combat

**Goal:** Roguelike turn-based combat. Enemies move when the player moves.

## New Files

### `Entity.kt`
- `Entity` class with:
  - Grid position (`x`, `y`)
  - `TileDef` for rendering
  - Stats: `hp`, `maxHp`, `attack`, `defense`
  - `takeDamage(amount)` — reduces HP by `max(1, amount - defense)`
  - `isAlive` property
- Used for both player and enemies

### `EnemyAi.kt`
- Simple chase AI per enemy:
  - If player within 10 tiles (Manhattan distance): move one grid step toward player, preferring the axis with greater distance
  - If adjacent to player: attack instead of moving
  - Otherwise: idle (no movement)
- Respects wall collision via `GameMap.isWalkable`
- Does not walk through other enemies

### `MessageLog.kt`
- Singleton collecting combat/event messages as strings
- `add(message)` appends to log
- `recent(n)` returns last N messages (default 5)
- Used for combat feedback: "Rat attacks you for 3 damage", "You killed the Skeleton"

## Modified Files

### `DungeonTileset.kt`
- Add enemy tile definitions with atlas coordinates:
  - `rat` — weak early enemy
  - `skeleton` — medium enemy
  - `demon` — strong enemy (deeper levels)

### `DungeonGenerator.kt`
- Enemy spawning: place 1–3 enemies per room (except the starting room)
- Enemy types scale with level depth:
  - Levels 1–2: rats
  - Levels 3–4: rats + skeletons
  - Level 5+: rats + skeletons + demons
- Returns enemy spawn positions and types alongside `GeneratedDungeon`

### `DungeonScene.kt`
- **Turn-based flow in `update()`:**
  1. Player input → determine target cell
  2. If target cell has an enemy: attack it (deal damage, log message)
  3. Else if walkable: move player
  4. After player acts, each living enemy takes one AI turn
  5. Dead enemies removed from entity list and no longer rendered
  6. If player dies → game over (switch to a restart scene or reset current dungeon)
- **Rendering:** Draw all living enemies on the map (between floor tiles and player)
- Player is now an `Entity` with HP/stats

### `Hud.kt`
- Add HP indicator (e.g., heart tiles or "HP: 20/20" text using tile sprites)
- Add message log display at bottom of screen (last 3–5 messages)

## Combat Formula

- Damage dealt = `attacker.attack - defender.defense` (minimum 1)
- Player starts with: 20 HP, 5 attack, 2 defense
- Rat: 5 HP, 2 attack, 0 defense
- Skeleton: 10 HP, 4 attack, 1 defense
- Demon: 18 HP, 7 attack, 3 defense

## Behavior

1. Player bumps into enemy → player attacks, damage message logged
2. After player's turn, each enemy moves/attacks
3. Enemies chase player when within 10 tiles
4. Killing an enemy removes it from the map
5. Player death triggers game over state
6. Health potions (from Phase 3) restore HP when consumed

## Verification

Run `./gradlew run` and confirm:
- Enemies visible in dungeon rooms (not in starting room)
- Bumping into enemy deals damage (message in log)
- Enemies chase and attack player after player moves
- Dead enemies disappear
- Player can die (game over state)
- HP shown in HUD, message log at bottom
