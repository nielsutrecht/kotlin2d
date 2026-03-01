## Context

The `Audio` object currently stores sounds in a flat `Map<String, Int>` (`sfxBuffers`) mapping name → OpenAL buffer. Loading scans only the top-level `sounds/` directory. The `sounds/dungeon/` subdirectory contains variant files like `walk-stone-1.ogg`, `walk-stone-2.ogg`, `walk-stone-3.ogg` that are natural groups but aren't loaded or groupable today.

## Goals / Non-Goals

**Goals:**
- Load sounds recursively from `sounds/` subdirectories
- Automatically group files matching `{name}-{N}.ogg` convention
- Play a random variant from a group via the existing `playSound` API
- Avoid repeating the last-played variant within a group

**Non-Goals:**
- Weighted/prioritized variant selection
- Making individual variants addressable by full name
- Changing the music system
- Adding new sound files (just making existing ones usable)

## Decisions

**1. Convention: trailing `-{digits}` suffix indicates a group member**

Detection regex: `^(.*)-(\d+)$` applied to the sound name (path without extension). All files sharing the same prefix are grouped. Files without a numeric suffix remain singletons.

Alternative considered: explicit group registration via config file. Rejected — adds complexity with no benefit when the naming convention is already consistent.

**2. Unified storage with a `SoundEntry` sealed type**

Replace `sfxBuffers: Map<String, Int>` with `sfxEntries: Map<String, SoundEntry>` where:
- `SoundEntry.Single(buffer: Int)` — one buffer
- `SoundEntry.Group(buffers: List<Int>, lastIndex: Int)` — multiple buffers with repetition tracking

`playSound` checks the entry type and dispatches accordingly. Callers don't change.

Alternative considered: separate maps for singles and groups. Rejected — splits lookup logic and complicates the API.

**3. Recursive directory walk for loading**

Replace `File.listFiles()` with `File.walk()` to discover all `.ogg` files under `sounds/`. The key becomes the relative path without extension (e.g., `sounds/dungeon/walk-stone-1.ogg` → `dungeon/walk-stone-1`). After loading all buffers, a grouping pass collects entries by their prefix.

**4. Repetition avoidance: exclude last-played index**

Same approach as the existing music system. Filter out the last-played index, pick randomly from the rest. With 2 variants this alternates; with 3+ it just avoids immediate repetition. `lastIndex` is stored mutably on the `Group` entry.

## Risks / Trade-offs

- [Naming collision] A singleton named `foo` and a group `foo-1`, `foo-2` would conflict on the key `foo` → Mitigation: grouping pass overwrites the singleton. In practice this won't happen with current naming conventions.
- [Mutable state in sealed class] `lastIndex` on `Group` is mutable → Acceptable for a single-threaded game loop. No thread safety concerns.
