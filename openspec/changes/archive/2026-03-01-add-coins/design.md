## Context

Chests currently drop 0-3 items (KEY, HEALTH_POTION, SWORD) that scatter onto the 4 cardinal floor tiles, with inventory fallback when no floor tiles are available. There is no currency system. The HUD shows level and inventory icons. Sound files in `sounds/items/` need conversion from MP3 to OGG.

## Goals / Non-Goals

**Goals:**
- Add a gold counter to `GameState` and display it on the HUD
- Add `CoinPile` entity that sits on the floor with a variable amount
- Chests drop a single coin pile (0-1000 gold) alongside items when opened
- Improve the scatter algorithm to BFS outward, always finding a free floor tile
- Convert coin sound effects and wire them up

**Non-Goals:**
- Coins from enemy drops or other sources
- Shop system or spending gold
- Different coin denominations or sprites based on amount
- Gold varying by dungeon level

## Decisions

**1. CoinPile as a standalone entity**

`data class CoinPile(var x: Int, var y: Int, val amount: Int)`

Not an `ItemType` — gold is a counter, not an inventory item. Coin piles live in their own list in `GeneratedDungeon`, `CachedLevel`, and `DungeonScene`, following the same pattern as items, enemies, and chests.

**2. Gold field on GameState**

`GameState.gold: Int` — simple counter, reset to 0 on `reset()`. No maximum. Displayed on the HUD with a coin icon tile.

**3. BFS scatter replaces cardinal-only scatter**

The current scatter tries 4 cardinal neighbors and falls back to inventory. The new approach uses BFS outward from the chest position: for each drop (items + coin pile), find the nearest walkable floor tile not occupied by another entity, the player, or a previous drop. This guarantees all drops land on the floor.

The BFS uses a queue starting from the chest position, expanding to all 8 neighbors (cardinal + diagonal), skipping walls, occupied tiles, the player position, and tiles already claimed by earlier drops in this scatter.

**4. Coin pile created at chest-open time, not generation time**

Unlike item contents (decided at generation), the coin pile amount is rolled when the chest opens. This keeps the `Chest` data class unchanged — it doesn't need a `gold` field. The pile is simply created and added to the scene's coin pile list during the scatter.

**5. Coin sounds**

- `items/coin-drop` (singleton): played when chest scatters a coin pile onto the floor
- `items/coin-take` (sound group, 2 variants): played when player walks onto a coin pile

Both are loaded automatically by the recursive sound loading system after MP3→OGG conversion.

**6. Coin tile at (59, 23)**

Added as `DungeonTileset.coinPile` with `TileKind.ITEM`.

## Risks / Trade-offs

- [BFS performance] BFS runs once per chest open, searching a small area — negligible cost for a turn-based movement system
- [Coin pile on top of items] A coin pile and item could theoretically land on the same tile from the same chest — BFS tracks claimed tiles to prevent this
