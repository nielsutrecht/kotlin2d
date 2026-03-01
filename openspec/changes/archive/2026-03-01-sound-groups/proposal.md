## Why

Playing the same sound effect every time an action occurs (footstep, door open, chest open) sounds robotic and repetitive. The `sounds/dungeon/` directory already contains multiple variants of each sound (e.g., `walk-stone-1.ogg` through `walk-stone-3.ogg`), but the audio system has no way to treat them as a group and pick a random variant.

## What Changes

- Recursive scanning of the `sounds/` directory so subdirectory files (e.g., `sounds/dungeon/`) are discovered and loaded
- Convention-based sound grouping: files matching `{name}-{N}.ogg` are automatically grouped under `{name}`
- `Audio.playSound("dungeon/walk-stone")` picks a random variant from the group, avoiding the last-played variant
- Singleton sounds (no numeric suffix) continue to work as before
- Individual variants are not directly addressable — only the group name is used

## Capabilities

### New Capabilities
- `sound-groups`: Convention-based grouping of sound variants with random selection and repetition avoidance

### Modified Capabilities
- `sfx-system`: Sound loading changes from flat directory scan to recursive, and `playSound` resolves group names to random variants

## Impact

- `Audio.kt`: Loading logic, sound storage, and `playSound` method all change
- `DungeonScene.kt`: Call sites can start using group names for dungeon sounds (footsteps, doors, chests)
- No new dependencies
