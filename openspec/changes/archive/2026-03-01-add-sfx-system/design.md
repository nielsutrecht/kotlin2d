## Context

The `Audio` object already manages OpenAL lifecycle (device, context) and music playback with a single source. It has a `decodeOgg` function that loads OGG files into AL buffers. SFX needs to coexist with the music system, sharing the same OpenAL device/context.

Sound effect files are OGG files in `sounds/` at the project root (same filesystem-loading pattern as `music/`). Currently available: `key.ogg`, `potion.ogg`.

## Goals / Non-Goals

**Goals:**
- General-purpose SFX system that works for any game event (pickups, doors, combat, etc.)
- Fire-and-forget API: `Audio.playSound("key")` — no cleanup needed at the call site
- Pre-decoded buffer cache for instant playback
- Fixed source pool to support multiple simultaneous sounds

**Non-Goals:**
- Positional/3D audio
- Volume control per sound or per category
- Sound priority system
- Looping sound effects (ambient loops)

## Decisions

### Fixed source pool of 8 sources
Allocate 8 AL sources for SFX at init time. When `playSound()` is called, find a stopped or unused source, attach the buffer, and play. If all sources are busy, steal the oldest playing source.

**Alternative considered:** Dynamic source creation — rejected because it adds complexity and 8 concurrent sounds is more than enough for a dungeon crawler.

### Preload all sounds at init
Scan `sounds/` directory, decode every OGG into an AL buffer, store in `Map<String, Int>`. Sound files are tiny (KB range), so loading all upfront is fine.

**Alternative considered:** Lazy loading on first play — adds latency on first use and complexity for negligible memory savings.

### Filename as sound ID
`sounds/key.ogg` → `Audio.playSound("key")`. No configuration file needed. Adding a new sound is just dropping an OGG file in the directory.

### Reuse existing `decodeOgg` for SFX loading
The same stb_vorbis decode path works for both music tracks and short sound effects — no new loading code needed.

## Risks / Trade-offs

- **[Pool exhaustion]** → If all 8 sources are playing, the oldest is stopped and reused. Acceptable — the player won't notice a distant sound cutting off
- **[Missing sound file]** → `playSound("nonexistent")` should silently no-op (print warning), not crash the game
