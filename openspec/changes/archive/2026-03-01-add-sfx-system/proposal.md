## Why

The game has background music but no sound effects — item pickups, door opens, and other actions are silent. A general-purpose SFX system makes it easy to add sounds to any game event by dropping OGG files in a directory.

## What Changes

- Extend the `Audio` object with a sound effect subsystem: a fixed pool of OpenAL sources, a preloaded buffer cache, and a `playSound(name)` API
- Discover and preload all OGG files from `sounds/` directory at init time, keyed by filename (e.g., `key.ogg` → `"key"`)
- Play item pickup sounds (`key`, `potion`) when the player picks up items in `DungeonScene`
- Reuse the existing `decodeOgg` function for loading sound effect buffers

## Capabilities

### New Capabilities
- `sfx-system`: General SFX playback with source pooling, buffer caching, and fire-and-forget API

### Modified Capabilities

## Impact

- `Audio.kt` — add SFX source pool, buffer cache, `playSound()`, update pool reclamation
- `DungeonScene.kt` — call `Audio.playSound()` on item pickup
