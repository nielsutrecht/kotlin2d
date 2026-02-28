# Phase 5: Sound & Music

**Goal:** Audio atmosphere using OpenAL via LWJGL.

## New Files

### `WavLoader.kt`
- Minimal RIFF/WAV parser
- Reads WAV files from classpath resources
- Extracts PCM data, sample rate, channel count, bit depth
- Returns an OpenAL buffer ID ready for playback

### `SoundManager.kt`
- Manages OpenAL device, context, and audio sources
- Source pool: 8 sources for SFX + 1 dedicated source for music
- API:
  - `init()` — opens OpenAL device and context
  - `loadSound(name: String, resourcePath: String)` — loads a WAV into a named buffer
  - `playSound(name: String)` — plays a sound effect (picks available source from pool)
  - `playMusic(name: String)` — plays looping background music on the music source
  - `stopMusic()` — stops current music
  - `dispose()` — releases all OpenAL resources (buffers, sources, context, device)

## Modified Files

### `build.gradle.kts`
- Add OpenAL dependencies:
  ```kotlin
  implementation("org.lwjgl:lwjgl-openal")
  runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
  ```

### `GameApp.kt`
- Call `SoundManager.init()` after OpenGL setup in `init()`
- Call `SoundManager.dispose()` in `cleanup()` before GLFW teardown

### `DungeonScene.kt`
- Add sound triggers:
  - **Footstep:** on every successful player move
  - **Attack/hit:** on player or enemy attack
  - **Pickup:** when collecting an item
  - **Door creak:** when opening a door
  - **Stairs tone:** when using stairs
  - **Enemy death:** when an enemy is killed
  - **Player death:** on game over
  - **Background music:** start dungeon music loop in `onEnter()`, stop in `onExit()`

## Audio Assets

Place WAV files in `src/main/resources/sounds/`:
- Format: 16-bit mono, 22050 Hz (small file size, retro feel)
- Can be generated with [jsfxr](https://sfxr.me/) or similar retro sound generators

Expected sound files:
| File | Trigger |
|------|---------|
| `footstep.wav` | Player movement |
| `attack.wav` | Player attacks enemy |
| `hit.wav` | Enemy attacks player |
| `pickup.wav` | Item collected |
| `door.wav` | Door opened |
| `stairs.wav` | Stairs used |
| `enemy_death.wav` | Enemy killed |
| `player_death.wav` | Player dies |
| `dungeon_music.wav` | Background music loop |

## Implementation Notes

- SoundManager is a singleton (`object`) for easy access from scenes
- Music source uses `AL_LOOPING = AL_TRUE` for continuous playback
- SFX sources are reused from a pool — find the first non-playing source
- If all SFX sources are busy, the oldest one is restarted with the new sound
- WAV loader only needs to support uncompressed PCM (RIFF format) — no need for MP3/OGG
- OpenAL initialization should fail gracefully (log warning, game continues without sound) if no audio device is available

## Verification

Run `./gradlew run` and confirm:
- Background dungeon music loops continuously
- Footstep sound plays on each player movement
- Attack and hit sounds play during combat
- Pickup sound on item collection
- Door creak when opening doors
- Stairs tone when changing levels
- Enemy death sound when killing enemies
- Sounds don't crash or stutter during normal gameplay
