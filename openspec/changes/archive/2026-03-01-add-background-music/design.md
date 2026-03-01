## Context

The game currently has no audio. LWJGL provides `lwjgl-openal` for audio playback and the already-included `lwjgl-stb` contains `stb_vorbis` for OGG decoding. Four OGG music tracks exist in `music/` at the project root.

The project uses a global object pattern for cross-cutting concerns (`Input`, `GameState`). Audio fits this pattern — it's independent of scenes and runs continuously.

## Goals / Non-Goals

**Goals:**
- Play background music continuously from game start
- Randomly select tracks with no immediate repeats
- Clean integration into the existing game loop and shutdown

**Non-Goals:**
- Sound effects (separate concern, different playback pattern)
- Per-scene or context-sensitive music (e.g., combat music)
- Volume controls or audio settings UI
- Music crossfading between tracks

## Decisions

### Global `Audio` object matching the `Input` pattern
A singleton `Audio` object manages the OpenAL device, context, source, and buffers. Initialized once in `GameApp`, updated each frame, cleaned up on exit.

**Alternative considered:** Per-scene audio manager — rejected because music plays continuously across scene transitions.

### Full OGG decode into AL buffer (not streaming)
Decode the entire OGG file into memory and load it into a single OpenAL buffer. Music tracks for indie games are typically a few MB decoded — well within memory.

**Alternative considered:** Streaming with buffer queues — more complex, only needed for very large files. These tracks are small enough for full decode.

### Poll `AL_SOURCE_STATE` each frame for track-end detection
Check the source state in `Audio.update()` called from the game loop. When `AL_STOPPED` is detected, pick the next track and play it.

**Alternative considered:** OpenAL callbacks — not available in the standard OpenAL API exposed by LWJGL.

### Move OGG files to classpath resources
Copy music files to `src/main/resources/music/` for consistent resource loading matching the texture pattern.

## Risks / Trade-offs

- **[Memory usage]** → Full decode loads entire tracks into memory. At ~10MB per minute of CD-quality audio, a 3-minute track is ~30MB decoded. Acceptable for 4 tracks since only one buffer is active at a time (previous buffer is deleted).
- **[Decode latency on track switch]** → Brief pause while decoding next OGG. Imperceptible for these file sizes. Could pre-decode next track if it becomes noticeable.
- **[OpenAL device unavailable]** → Some systems have no audio device. Audio init should handle this gracefully and let the game run silently.
