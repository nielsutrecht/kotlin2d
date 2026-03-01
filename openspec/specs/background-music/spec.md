## ADDED Requirements

### Requirement: Audio system initializes OpenAL
The `Audio` object SHALL initialize an OpenAL device and context on `init()`. If no audio device is available, the system SHALL log a warning and continue without audio. The game MUST NOT crash due to missing audio hardware.

#### Scenario: Successful audio initialization
- **WHEN** `Audio.init()` is called on a system with audio hardware
- **THEN** an OpenAL device and context SHALL be created and made current

#### Scenario: No audio device available
- **WHEN** `Audio.init()` is called on a system without audio hardware
- **THEN** the system SHALL log a warning and all subsequent `Audio` calls SHALL be no-ops

### Requirement: Music tracks are loaded from classpath resources
The system SHALL discover all `.ogg` files in the `music/` resource directory. The available tracks are:
- `Abnormal Circumstances.ogg`
- `ClockWork.ogg`
- `Element.ogg`
- `HeatOfBattle.ogg`

#### Scenario: Track discovery
- **WHEN** the audio system initializes
- **THEN** all OGG files in `src/main/resources/music/` SHALL be available for playback

### Requirement: A random track plays on game start
The system SHALL randomly select one track from the available tracks and begin playback immediately after audio initialization.

#### Scenario: Music starts on launch
- **WHEN** the game finishes initializing
- **THEN** a randomly selected music track SHALL be playing

### Requirement: Next track is randomly selected excluding the current track
When a track finishes playing, the system SHALL randomly select another track from all available tracks excluding the one that just finished, and begin playing it immediately.

#### Scenario: Track ends and next plays
- **WHEN** the currently playing track finishes
- **THEN** a different track SHALL begin playing, selected randomly from the remaining tracks

#### Scenario: No immediate repeat
- **WHEN** track A finishes playing
- **THEN** track A SHALL NOT be selected as the next track

### Requirement: Track end is detected by polling
The `Audio.update()` method SHALL be called each frame from the game loop. It SHALL check the OpenAL source state and trigger next-track selection when the state is `AL_STOPPED`.

#### Scenario: Source still playing
- **WHEN** `Audio.update()` is called and the source state is `AL_PLAYING`
- **THEN** no action SHALL be taken

#### Scenario: Source stopped
- **WHEN** `Audio.update()` is called and the source state is `AL_STOPPED`
- **THEN** the next track SHALL be selected and playback SHALL begin

### Requirement: Audio resources are cleaned up on shutdown
The `Audio.cleanup()` method SHALL stop playback and release all OpenAL resources (source, buffers, context, device).

#### Scenario: Clean shutdown
- **WHEN** `Audio.cleanup()` is called
- **THEN** the OpenAL source, buffers, context, and device SHALL be destroyed
