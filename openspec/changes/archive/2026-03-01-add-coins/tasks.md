## 1. Sound Assets

- [x] 1.1 Convert `sounds/items/*.mp3` to OGG format and delete MP3 originals

## 2. Data Model & Tileset

- [x] 2.1 Create `CoinPile.kt` with `data class CoinPile(var x: Int, var y: Int, val amount: Int)`
- [x] 2.2 Add `coinPile` tile definition at (59, 23) to `DungeonTileset`
- [x] 2.3 Add `gold: Int` field to `GameState`, reset to 0 in `reset()`
- [x] 2.4 Add coin piles list to `GeneratedDungeon` and `CachedLevel`

## 3. BFS Scatter

- [x] 3.1 Replace the cardinal-only scatter in `DungeonScene` with a BFS expansion that finds free walkable tiles outward from the chest, skipping walls, occupied tiles, the player, and previously claimed tiles

## 4. Chest Coin Drops

- [x] 4.1 When a chest opens, roll a random coin amount (0-1000); if > 0, create a `CoinPile` on a BFS-found tile, play `items/coin-drop` sound

## 5. Coin Pickup

- [x] 5.1 Add coin pile pickup in `DungeonScene.update()`: when player walks onto a coin pile, add amount to `GameState.gold`, play `items/coin-take`, remove pile

## 6. Rendering & HUD

- [x] 6.1 Add coin pile rendering in `DungeonScene.render()` using `coinPile` tile definition
- [x] 6.2 Add gold counter to `Hud.render()` showing coin icon and numeric amount
- [x] 6.3 Wire coin piles into `CachedLevel` save/restore in `DungeonScene`
