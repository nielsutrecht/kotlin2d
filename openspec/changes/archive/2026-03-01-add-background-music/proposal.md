## Why

The dungeon is silent — adding background music creates atmosphere and makes exploration feel more immersive. Four OGG tracks already exist in `music/`.

## What Changes

- Add LWJGL OpenAL dependency for audio playback
- Copy OGG music files to `src/main/resources/music/` for classpath loading
- Create an `Audio` object that initializes OpenAL, decodes OGG files via stb_vorbis, and manages playback
- On startup, randomly pick a track and begin playing
- When a track ends, randomly pick another (excluding the one that just played) and play it
- Poll the OpenAL source state each frame from the game loop to detect track end
- Clean up OpenAL resources on shutdown

## Capabilities

### New Capabilities
- `background-music`: Audio initialization, OGG decoding, track playback with random no-repeat selection

### Modified Capabilities

## Impact

- `build.gradle.kts` — new `lwjgl-openal` dependency (implementation + natives)
- New `Audio.kt` — OpenAL lifecycle, OGG decoding, track management
- `GameApp.kt` — call `Audio.init()`, `Audio.update()` in game loop, `Audio.cleanup()` on shutdown
- `music/*.ogg` — move to `src/main/resources/music/`
