# Feature: Sound & Music

## Summary

Add audio atmosphere using OpenAL (via LWJGL). A background music loop plays in the dungeon, and all key interactions produce sound effects.

## Prerequisites

- Phase 4 (enemies, combat, message log)
- LWJGL OpenAL module added to the build

## Requirements

### WAV Loader

- Minimal RIFF/WAV parser (`WavLoader.kt`).
- Reads uncompressed PCM WAV files from classpath resources.
- Extracts sample rate, channel count, and bit depth; returns an OpenAL buffer ID.

### Sound Manager

- `SoundManager` singleton managing the OpenAL lifecycle.
- Source pool: 8 sources for SFX, 1 dedicated source for looping music.
- Public API:

| Method | Description |
|--------|-------------|
| `init()` | Open device and context |
| `loadSound(name, path)` | Load a WAV into a named buffer |
| `playSound(name)` | Play SFX on the next available source |
| `playMusic(name)` | Loop music on the music source |
| `stopMusic()` | Stop current music |
| `dispose()` | Release all OpenAL resources |

- Fails gracefully (logs a warning, game continues silently) if no audio device is available.
- When all SFX sources are busy, the oldest is restarted with the new sound.

### Sound Triggers

| Event | Sound |
|-------|-------|
| Player moves | `footstep.wav` |
| Player attacks | `attack.wav` |
| Enemy attacks player | `hit.wav` |
| Item collected | `pickup.wav` |
| Door opened | `door.wav` |
| Stairs used | `stairs.wav` |
| Enemy killed | `enemy_death.wav` |
| Player dies | `player_death.wav` |
| Scene entered | `dungeon_music.wav` (loop) |

### Audio Assets

- Location: `src/main/resources/sounds/`
- Format: 16-bit mono PCM, 22050 Hz.
- Can be generated with [jsfxr](https://sfxr.me/) or similar retro sound tools.

## File Changes

| File | Action | Detail |
|------|--------|--------|
| `WavLoader.kt` | Create | RIFF/WAV parser returning OpenAL buffer IDs |
| `SoundManager.kt` | Create | OpenAL lifecycle, source pool, play/stop API |
| `build.gradle.kts` | Modify | Add `lwjgl-openal` implementation and runtime deps |
| `GameApp.kt` | Modify | Call `SoundManager.init()` / `dispose()` |
| `DungeonScene.kt` | Modify | Wire all sound triggers listed above |

## Acceptance Criteria

1. Background music loops continuously while in the dungeon.
2. Each player movement produces a footstep sound.
3. Attack and hit sounds play during combat.
4. Picking up an item plays a pickup sound.
5. Opening a door plays a door creak.
6. Using stairs plays a transition tone.
7. Enemy death produces a sound.
8. The game runs without crashes or audio stutter.
9. The game still launches if no audio device is present (graceful fallback).

## Verification

```bash
./gradlew run
```

- Music loops on scene enter, stops on scene exit.
- All listed sound effects fire at the correct moment.
- No audio glitches during normal gameplay.
