## ADDED Requirements

### Requirement: Sound effects are preloaded from the sounds directory
The system SHALL scan the `sounds/` directory at init time and decode all OGG files into OpenAL buffers. Each sound SHALL be keyed by its filename without extension (e.g., `key.ogg` → `"key"`).

#### Scenario: Sound discovery at init
- **WHEN** `Audio.init()` is called and the `sounds/` directory contains `key.ogg` and `potion.ogg`
- **THEN** both sounds SHALL be decoded and available for playback via `"key"` and `"potion"`

#### Scenario: Empty sounds directory
- **WHEN** `Audio.init()` is called and the `sounds/` directory is empty or missing
- **THEN** no SFX buffers SHALL be loaded, and the game SHALL continue without sound effects

### Requirement: A fixed pool of OpenAL sources is used for SFX
The system SHALL allocate a fixed pool of 8 OpenAL sources for sound effect playback at init time. Sources SHALL be reused when they finish playing.

#### Scenario: Concurrent sounds within pool size
- **WHEN** 4 sounds are triggered in quick succession
- **THEN** each sound SHALL play on a separate source simultaneously

#### Scenario: Pool exhaustion
- **WHEN** all 8 sources are currently playing and a new sound is triggered
- **THEN** the oldest playing source SHALL be stopped and reused for the new sound

### Requirement: Sounds are played via fire-and-forget API
The system SHALL provide an `Audio.playSound(name: String)` method that immediately plays the named sound. No cleanup or tracking SHALL be required at the call site.

#### Scenario: Play a known sound
- **WHEN** `Audio.playSound("key")` is called
- **THEN** the key sound SHALL play immediately using an available source from the pool

#### Scenario: Play an unknown sound
- **WHEN** `Audio.playSound("nonexistent")` is called
- **THEN** a warning SHALL be printed and no sound SHALL play — the game MUST NOT crash

#### Scenario: Audio unavailable
- **WHEN** `Audio.playSound()` is called but no audio device was found during init
- **THEN** the call SHALL be a no-op

### Requirement: SFX resources are cleaned up on shutdown
The `Audio.cleanup()` method SHALL stop all SFX sources, delete the source pool, and delete all cached sound buffers in addition to existing music cleanup.

#### Scenario: Clean shutdown with SFX
- **WHEN** `Audio.cleanup()` is called
- **THEN** all SFX sources and buffers SHALL be destroyed alongside music resources

### Requirement: Item pickup triggers a sound effect
When the player picks up an item, the system SHALL play a sound effect matching the item type name. The mapping is:
- KEY → `Audio.playSound("key")`
- HEALTH_POTION → `Audio.playSound("potion")`

#### Scenario: Pick up a key
- **WHEN** the player steps onto a tile containing a KEY item
- **THEN** the `"key"` sound effect SHALL play

#### Scenario: Pick up a potion
- **WHEN** the player steps onto a tile containing a HEALTH_POTION item
- **THEN** the `"potion"` sound effect SHALL play

#### Scenario: Pick up an item with no matching sound
- **WHEN** the player picks up a SWORD and no `"sword"` sound file exists
- **THEN** no sound SHALL play (the missing sound warning is printed, game continues)
