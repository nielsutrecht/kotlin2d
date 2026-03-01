## 1. SFX Infrastructure

- [x] 1.1 Add SFX source pool (8 sources) to `Audio.init()` — allocate sources, track usage order for pool stealing
- [x] 1.2 Add SFX buffer cache — scan `sounds/` directory at init, decode all OGG files via existing `decodeOgg`, store in `Map<String, Int>`
- [x] 1.3 Implement `Audio.playSound(name)` — look up buffer by name, find available source (or steal oldest), attach and play. No-op if audio unavailable or name unknown (print warning).
- [x] 1.4 Update `Audio.cleanup()` to delete SFX sources and buffers

## 2. Integration

- [x] 2.1 Play item pickup sounds in `DungeonScene.update()` — `"key"` for KEY, `"potion"` for HEALTH_POTION
