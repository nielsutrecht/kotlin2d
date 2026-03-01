## ADDED Requirements

### Requirement: Chest entity tracks position, state, and contents
A chest SHALL be represented as an entity with x/y position, an opened flag, and a list of `ItemType` contents. Chests SHALL be stored as a mutable list alongside items and enemies in the dungeon data.

#### Scenario: Chest data structure
- **WHEN** a chest is created at position (5, 10) with contents [KEY, HEALTH_POTION]
- **THEN** the chest SHALL have x=5, y=10, opened=false, and contents containing KEY and HEALTH_POTION

### Requirement: Chests have closed and open tile definitions
The tileset SHALL define `chestClosed` at atlas coordinates (44, 45) and `chestOpen` at atlas coordinates (45, 45), both with `TileKind.CHEST`.

#### Scenario: Closed chest rendering
- **WHEN** a chest with opened=false is in the visible range
- **THEN** it SHALL be rendered using the `chestClosed` tile definition

#### Scenario: Open chest rendering
- **WHEN** a chest with opened=true is in the visible range
- **THEN** it SHALL be rendered using the `chestOpen` tile definition

### Requirement: Chests spawn sparingly in dungeon rooms
The dungeon generator SHALL place at most one chest per room, skipping the first room. Each eligible room SHALL have a low probability of containing a chest (~20%). Chests SHALL be placed on unoccupied floor tiles.

#### Scenario: Chest placement in a room
- **WHEN** a room is eligible for chest placement and the random check succeeds
- **THEN** exactly one chest SHALL be placed on an unoccupied floor tile within that room

#### Scenario: First room has no chests
- **WHEN** the dungeon is generated
- **THEN** the first room (player spawn) SHALL NOT contain any chests

#### Scenario: Occupied tile avoidance
- **WHEN** a chest placement is attempted at a position already occupied by an item or enemy
- **THEN** the chest SHALL NOT be placed at that position

### Requirement: Chest contents are generated at spawn time
Each chest SHALL contain 0 to 3 random items, with at most one of each type (KEY, HEALTH_POTION, SWORD). Contents SHALL be determined when the chest is created by the generator.

#### Scenario: Maximum variety chest
- **WHEN** a chest is generated and the random roll produces 3 items
- **THEN** the chest SHALL contain exactly one KEY, one HEALTH_POTION, and one SWORD

#### Scenario: Empty chest
- **WHEN** a chest is generated and the random roll produces 0 items
- **THEN** the chest SHALL have an empty contents list

### Requirement: Player opens a chest by walking into it
When the player attempts to move onto a tile containing a closed chest, the chest SHALL open, play the `dungeon/chest-open` sound, and scatter its contents and a coin pile onto nearby floor tiles using BFS expansion. The player SHALL NOT move onto the chest tile.

#### Scenario: Open a closed chest
- **WHEN** the player walks into a tile containing a closed chest with contents [KEY, SWORD] and a coin roll of 500
- **THEN** the chest SHALL become opened, the `dungeon/chest-open` sound SHALL play, KEY and SWORD items SHALL appear on nearby floor tiles, and a coin pile of 500 SHALL appear on a nearby floor tile

#### Scenario: Walk into an already-opened chest
- **WHEN** the player walks into a tile containing an opened chest
- **THEN** the player SHALL NOT move and no sound SHALL play

#### Scenario: Chest blocks movement
- **WHEN** a chest (opened or closed) is at the target position
- **THEN** the player SHALL NOT move onto that tile

### Requirement: Chest contents scatter onto adjacent floor tiles
When a chest is opened, its contents (items and coin pile) SHALL be placed on walkable floor tiles near the chest using BFS expansion outward. The search SHALL expand from the chest position checking all 8 neighbors (cardinal and diagonal), skipping walls, tiles occupied by entities, the player position, and tiles already claimed by earlier drops. Every drop SHALL find a free floor tile.

#### Scenario: Drops placed via BFS
- **WHEN** a chest opens with 3 items and a coin pile, and the 4 cardinal neighbors are all occupied
- **THEN** the BFS SHALL expand to diagonal and further tiles, placing all 4 drops on free floor tiles

#### Scenario: Tight space
- **WHEN** a chest opens in a narrow corridor with limited floor space
- **THEN** the BFS SHALL expand outward until free tiles are found for all drops

### Requirement: Chests persist in level cache
Chests SHALL be included in the `CachedLevel` data so their state (opened/closed, remaining contents) persists when the player leaves and returns to a level.

#### Scenario: Return to level with opened chest
- **WHEN** the player opens a chest on level 1, descends to level 2, then returns to level 1
- **THEN** the chest SHALL still be opened with no contents
