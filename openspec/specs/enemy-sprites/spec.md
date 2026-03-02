## ADDED Requirements

### Requirement: Enemy types are defined in the tileset
The system SHALL define five enemy types — GREEN_SLIME, SKELETON, BAT, RAT, and GOBLIN — each with a corresponding `TileDef` in `DungeonTileset` using the following atlas coordinates from the existing sprite sheet. A new `TileKind.ENEMY` value SHALL be added.

| Enemy | atlasX | atlasY |
|-------|--------|--------|
| GREEN_SLIME | 49 | 5 |
| SKELETON | 22 | 8 |
| BAT | 44 | 3 |
| RAT | 64 | 3 |
| GOBLIN | 45 | 2 |

#### Scenario: Enemy tile definitions exist
- **WHEN** the game initializes
- **THEN** `DungeonTileset` SHALL contain tile definitions for `greenSlime` (49,5), `skeleton` (22,8), `bat` (44,3), `rat` (64,3), and `goblin` (45,2), each with `TileKind.ENEMY`

### Requirement: Enemy data model stores position and type
The system SHALL represent enemies with an `Enemy` data class containing `type: EnemyType`, `x: Int`, `y: Int`, base stats (`hp`, `attack`, `defense`), AI state (`state: EnemyState`, default IDLE), spawn room reference (`spawnRoom: Room`), and a turn skip counter (`turnCounter: Int`, default 0) for speed control.

#### Scenario: Enemy instance creation
- **WHEN** an enemy is created with `Enemy(EnemyType.SKELETON, x=5, y=10, spawnRoom=room)`
- **THEN** the enemy SHALL have the skeleton's default stats, the specified grid position, IDLE state, and the given spawn room reference

### Requirement: Enemies spawn in dungeon rooms during generation
The dungeon generator SHALL place enemies in rooms during level generation. Each room except the first (starting) room SHALL have a chance to contain 1-3 enemies. Enemies SHALL NOT be placed on walls, doors, stairs, items, or other enemies.

#### Scenario: Enemies spawn in non-starting rooms
- **WHEN** a dungeon level is generated
- **THEN** rooms other than the first room SHALL contain between 0 and 3 enemies placed on walkable floor tiles

#### Scenario: Starting room has no enemies
- **WHEN** a dungeon level is generated
- **THEN** the first room (where the player spawns) SHALL contain zero enemies

#### Scenario: Enemies avoid occupied tiles
- **WHEN** an enemy spawn position is selected
- **THEN** the position SHALL NOT overlap with walls, doors, stairs, items, or previously placed enemies

### Requirement: Enemy type availability scales with dungeon level
The generator SHALL select enemy types based on the current dungeon level:

| Level | Available enemies |
|-------|-------------------|
| 1 | GREEN_SLIME, RAT |
| 2 | GREEN_SLIME, RAT, SKELETON, GOBLIN |
| 3+ | GREEN_SLIME, RAT, SKELETON, GOBLIN, BAT |

#### Scenario: Level 1 enemies
- **WHEN** enemies are generated on level 1
- **THEN** all enemies SHALL be of type GREEN_SLIME or RAT

#### Scenario: Level 2 enemies
- **WHEN** enemies are generated on level 2
- **THEN** enemies SHALL be GREEN_SLIME, RAT, SKELETON, or GOBLIN

#### Scenario: Level 3+ enemies
- **WHEN** enemies are generated on level 3 or higher
- **THEN** enemies SHALL be GREEN_SLIME, RAT, SKELETON, GOBLIN, or BAT

### Requirement: Enemies are rendered on the dungeon map
The dungeon scene SHALL render all enemies on the current level using their corresponding tile definitions. Enemies SHALL be rendered after map tiles and items, but before the player sprite.

#### Scenario: Enemy visible on screen
- **WHEN** an enemy's grid position is within the camera's visible range
- **THEN** the enemy's sprite SHALL be drawn at the correct screen position using the batched tile renderer

#### Scenario: Enemy off-screen is culled
- **WHEN** an enemy's grid position is outside the camera's visible range
- **THEN** the enemy's sprite SHALL NOT be rendered

### Requirement: Enemies persist when changing levels
Enemies SHALL be cached per level in `GameState.dungeonCache` alongside the map, items, and player position. When the player returns to a previously visited level, the cached enemy list SHALL be restored.

#### Scenario: Backtracking preserves enemies
- **WHEN** the player leaves level 1, visits level 2, then returns to level 1
- **THEN** the enemies on level 1 SHALL be the same as when the player left
