## ADDED Requirements

### Requirement: Enemies move one step each time the player moves
The system SHALL update all enemies once per player step. When the player successfully moves to a new tile, every living enemy SHALL execute one AI tick before the frame renders.

#### Scenario: Enemy moves after player step
- **WHEN** the player moves one tile in any direction
- **THEN** all enemies on the current level SHALL each take one movement step according to their AI state

#### Scenario: Enemies do not move when player is idle
- **WHEN** the player does not move (no key pressed or cooldown not elapsed)
- **THEN** no enemies SHALL move

### Requirement: Enemies have IDLE and CHASING states
Each enemy SHALL have an AI state: IDLE or CHASING. An enemy in IDLE state wanders within its spawn area. An enemy in CHASING state pursues the player.

#### Scenario: Enemy starts in IDLE state
- **WHEN** a dungeon level is generated and enemies are placed
- **THEN** all enemies SHALL begin in the IDLE state

#### Scenario: Enemy transitions to CHASING when player is detected
- **WHEN** an IDLE enemy has line-of-sight to the player AND the player is within the enemy's detection range
- **THEN** the enemy SHALL transition to CHASING state

#### Scenario: Enemy returns to IDLE when line-of-sight is lost
- **WHEN** a CHASING enemy no longer has line-of-sight to the player OR the player moves beyond detection range
- **THEN** the enemy SHALL transition back to IDLE state

### Requirement: Line-of-sight detection uses grid raycast
The system SHALL determine line-of-sight by walking a straight line from the enemy's tile to the player's tile. If any wall tile is encountered along the ray, LOS is blocked.

#### Scenario: Clear line-of-sight
- **WHEN** there are no wall tiles between an enemy at (5,5) and the player at (5,10)
- **THEN** the enemy SHALL have line-of-sight to the player

#### Scenario: Wall blocks line-of-sight
- **WHEN** a wall tile exists at (5,7) between an enemy at (5,5) and the player at (5,10)
- **THEN** the enemy SHALL NOT have line-of-sight to the player

### Requirement: Each enemy type has distinct behavior profiles
The system SHALL define per-type behavior for detection range, movement speed, and idle wander style:

| Type | Detection Range | Speed (moves per turn) | Idle Behavior |
|------|----------------|----------------------|---------------|
| GREEN_SLIME | 4 tiles | Every 2nd turn | Random wander |
| RAT | 5 tiles | Every turn | Random wander |
| BAT | 6 tiles | Every turn | Erratic (random even when chasing, 50% toward player) |
| GOBLIN | 7 tiles | Every turn | Room wander |
| SKELETON | 8 tiles | Every turn | Patrol (pace back and forth) |

#### Scenario: Green slime moves slowly
- **WHEN** a GREEN_SLIME takes an AI tick
- **THEN** it SHALL only move on every 2nd player step (skip alternating turns)

#### Scenario: Bat moves erratically while chasing
- **WHEN** a BAT is in CHASING state and takes an AI tick
- **THEN** it SHALL move toward the player 50% of the time and move randomly 50% of the time

#### Scenario: Skeleton patrols when idle
- **WHEN** a SKELETON is in IDLE state
- **THEN** it SHALL pace back and forth along a single axis within its spawn area

### Requirement: Chasing enemies use shared BFS pathfinding
The system SHALL compute a single BFS distance map from the player's position each turn. Chasing enemies SHALL move to the adjacent walkable tile with the lowest distance value. Tiles occupied by other enemies SHALL be treated as impassable in the BFS.

#### Scenario: Enemy follows optimal path around walls
- **WHEN** a CHASING enemy is separated from the player by a wall
- **THEN** the enemy SHALL navigate around the wall via the shortest walkable path

#### Scenario: Enemies do not stack
- **WHEN** a CHASING enemy's optimal next tile is occupied by another enemy
- **THEN** the enemy SHALL either choose an alternative adjacent tile or stay in place

### Requirement: Idle enemies stay within their spawn area
Enemies in IDLE state SHALL constrain their movement to their spawn room bounds. An enemy SHALL NOT wander into corridors or other rooms while idle.

#### Scenario: Idle enemy stays in room
- **WHEN** an IDLE enemy attempts to wander
- **THEN** it SHALL only move to floor tiles within its spawn room boundaries

### Requirement: Contact with player kills enemy and drops coins
When an enemy occupies the same tile as the player (either by the enemy moving onto the player or the player moving onto the enemy), the enemy SHALL be removed and a CoinPile with 1-20 gold SHALL be dropped at that position. A sound SHALL play on contact.

#### Scenario: Player walks into enemy
- **WHEN** the player moves onto a tile occupied by an enemy
- **THEN** the enemy SHALL be removed, a CoinPile(1-20) SHALL be placed at that tile, the player's gold SHALL increase, and a sound SHALL play

#### Scenario: Enemy walks into player
- **WHEN** a CHASING enemy moves onto the player's tile
- **THEN** the enemy SHALL be removed, a CoinPile(1-20) SHALL be placed at the player's tile, the player's gold SHALL increase, and a sound SHALL play

### Requirement: Enemies cannot move through walls or closed doors
Enemies SHALL only move to walkable tiles. Walls and closed doors SHALL block enemy movement. Open doors SHALL be passable.

#### Scenario: Enemy blocked by closed door
- **WHEN** an enemy attempts to move onto a tile with a closed door
- **THEN** the enemy SHALL remain in its current position
