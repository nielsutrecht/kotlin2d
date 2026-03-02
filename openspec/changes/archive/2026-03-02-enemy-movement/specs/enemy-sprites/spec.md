## MODIFIED Requirements

### Requirement: Enemy data model stores position and type
The system SHALL represent enemies with an `Enemy` data class containing `type: EnemyType`, `x: Int`, `y: Int`, base stats (`hp`, `attack`, `defense`), AI state (`state: EnemyState`, default IDLE), spawn room reference (`spawnRoom: Room`), and a turn skip counter (`turnCounter: Int`, default 0) for speed control.

#### Scenario: Enemy instance creation
- **WHEN** an enemy is created with `Enemy(EnemyType.SKELETON, x=5, y=10, spawnRoom=room)`
- **THEN** the enemy SHALL have the skeleton's default stats, the specified grid position, IDLE state, and the given spawn room reference
