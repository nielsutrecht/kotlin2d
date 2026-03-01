## ADDED Requirements

### Requirement: Sound files with numeric suffixes are automatically grouped
When loading sounds, files whose name (without extension) ends in `-{digits}` SHALL be grouped under the shared prefix. For example, `walk-stone-1.ogg`, `walk-stone-2.ogg`, and `walk-stone-3.ogg` SHALL form a group named `walk-stone`. Subdirectory paths SHALL be preserved in the group name (e.g., `dungeon/walk-stone`).

#### Scenario: Files with numeric suffixes form a group
- **WHEN** the `sounds/dungeon/` directory contains `chest-open-1.ogg`, `chest-open-2.ogg`, `chest-open-3.ogg`, and `chest-open-4.ogg`
- **THEN** a single group named `dungeon/chest-open` SHALL be created containing all 4 buffers

#### Scenario: Files without numeric suffix remain singletons
- **WHEN** the `sounds/` directory contains `key.ogg`
- **THEN** a singleton entry named `key` SHALL be created (no grouping)

#### Scenario: Mixed files in a directory
- **WHEN** a directory contains `hit.ogg`, `hit-1.ogg`, and `hit-2.ogg`
- **THEN** `hit` SHALL be a singleton and `hit-1`/`hit-2` SHALL form a group also named `hit`, with the group taking precedence

### Requirement: Playing a group sound selects a random variant
When `Audio.playSound` is called with a group name, the system SHALL randomly select one of the group's buffers and play it.

#### Scenario: Play a group sound
- **WHEN** `Audio.playSound("dungeon/walk-stone")` is called and the group has 3 variants
- **THEN** one of the 3 variants SHALL be selected at random and played

#### Scenario: Play a singleton sound
- **WHEN** `Audio.playSound("key")` is called and `key` is a singleton
- **THEN** the single buffer SHALL be played (unchanged behavior)

### Requirement: Group playback avoids repeating the last variant
When a group has more than one variant, the system SHALL NOT play the same variant that was played most recently for that group.

#### Scenario: Avoid immediate repetition with 3 variants
- **WHEN** `Audio.playSound("dungeon/walk-stone")` is called and the last played variant was index 1
- **THEN** the system SHALL select randomly from the remaining indices (0 and 2)

#### Scenario: Two-variant group alternates
- **WHEN** a group has exactly 2 variants and one was just played
- **THEN** the other variant SHALL always be selected

#### Scenario: First play has no restriction
- **WHEN** a group sound is played for the first time (no previous variant)
- **THEN** any variant MAY be selected

### Requirement: Individual group variants are not directly addressable
The system SHALL NOT expose individual variant names (e.g., `dungeon/walk-stone-1`) as playable sound names. Only the group name SHALL be usable.

#### Scenario: Attempt to play a variant by full name
- **WHEN** `Audio.playSound("dungeon/walk-stone-1")` is called
- **THEN** the system SHALL print a warning and no sound SHALL play
