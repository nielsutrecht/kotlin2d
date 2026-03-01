## MODIFIED Requirements

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
