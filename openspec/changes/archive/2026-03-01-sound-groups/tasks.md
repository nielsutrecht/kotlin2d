## 1. Data Model

- [x] 1.1 Add `SoundEntry` sealed class with `Single(buffer: Int)` and `Group(buffers: List<Int>, var lastIndex: Int = -1)` variants
- [x] 1.2 Replace `sfxBuffers: MutableMap<String, Int>` with `sfxEntries: MutableMap<String, SoundEntry>`

## 2. Recursive Loading & Grouping

- [x] 2.1 Change sound loading from `File.listFiles()` to `File("sounds").walk()` to discover OGG files recursively, keyed by relative path without extension
- [x] 2.2 After loading all buffers, run a grouping pass: detect names matching `^(.*)-(\d+)$`, collect into `SoundEntry.Group`, and remove individual variant entries
- [x] 2.3 Wrap remaining ungrouped buffers as `SoundEntry.Single`

## 3. Playback

- [x] 3.1 Update `playSound` to dispatch on `SoundEntry` type — `Single` plays directly, `Group` selects a random variant excluding `lastIndex`
- [x] 3.2 Update `cleanup` to handle `SoundEntry` types when deleting buffers

## 4. Integration

- [x] 4.1 Wire up dungeon sound groups in `DungeonScene` (footsteps, doors, chests) using group names like `dungeon/walk-stone`
