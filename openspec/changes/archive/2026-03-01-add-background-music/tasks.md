## 1. Dependencies & Assets

- [x] 1.1 Add `lwjgl-openal` implementation and runtime native dependencies to `build.gradle.kts`
- [x] 1.2 Copy OGG files from `music/` to `src/main/resources/music/`

## 2. Audio System

- [x] 2.1 Create `Audio.kt` — singleton object with OpenAL device/context init, graceful failure if no audio device
- [x] 2.2 Add OGG decoding via stb_vorbis — load an OGG resource into an OpenAL buffer
- [x] 2.3 Add track management — discover tracks, randomly pick one (excluding last played), play via AL source
- [x] 2.4 Add `update()` method — poll `AL_SOURCE_STATE`, trigger next track on `AL_STOPPED`
- [x] 2.5 Add `cleanup()` method — stop source, delete buffers/source/context/device

## 3. Integration

- [x] 3.1 Call `Audio.init()` in `GameApp` after window creation, `Audio.update()` in the game loop, and `Audio.cleanup()` on shutdown
