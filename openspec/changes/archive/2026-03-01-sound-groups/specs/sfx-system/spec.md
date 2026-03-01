## MODIFIED Requirements

### Requirement: Sound effects are preloaded from the sounds directory
The system SHALL recursively scan the `sounds/` directory at init time and decode all OGG files into OpenAL buffers. Each sound SHALL be keyed by its relative path without extension (e.g., `sounds/dungeon/walk-stone-1.ogg` → `dungeon/walk-stone-1`). After loading, files with numeric suffixes SHALL be grouped per the sound-groups capability.

#### Scenario: Sound discovery at init with subdirectories
- **WHEN** `Audio.init()` is called and the `sounds/` directory contains `key.ogg` and `dungeon/walk-stone-1.ogg`, `dungeon/walk-stone-2.ogg`
- **THEN** `key` SHALL be available as a singleton, and `dungeon/walk-stone` SHALL be available as a group with 2 variants

#### Scenario: Empty sounds directory
- **WHEN** `Audio.init()` is called and the `sounds/` directory is empty or missing
- **THEN** no SFX entries SHALL be loaded, and the game SHALL continue without sound effects
