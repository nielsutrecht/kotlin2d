## Why

The game has no currency system. Chests currently only drop items (keys, potions, swords), but coins are a fundamental dungeon reward that gives exploration more purpose and enables future features like shops.

## What Changes

- New `CoinPile` entity with position and amount, placed on the floor when chests open
- `GameState.gold` counter tracks the player's total gold
- Chests drop a single coin pile with a random amount (0–1000) alongside their item drops
- Coin pile tile definition at atlas coordinates (59, 23)
- Player picks up coin piles by walking onto them, adding the amount to their gold counter
- Coin sound effects: `items/coin-drop` on chest scatter, `items/coin-take` (sound group) on pickup
- Convert `sounds/items/*.mp3` to OGG format
- Improved scatter algorithm: BFS outward from chest to find free floor tiles, replacing the cardinal-only approach with inventory fallback
- HUD displays gold counter

## Capabilities

### New Capabilities
- `coins`: Coin pile entity, gold counter, pickup, and HUD display

### Modified Capabilities
- `chests`: Scatter algorithm changes from cardinal-only with inventory fallback to BFS expansion; chests now also drop a coin pile

## Impact

- `CoinPile.kt`: New entity file
- `DungeonTileset.kt`: Add coin pile tile definition
- `GameState.kt`: Add `gold` field
- `DungeonScene.kt`: Coin pile rendering, pickup logic, improved scatter algorithm
- `DungeonGenerator.kt`: No change (coin piles are created at chest-open time, not at generation)
- `CachedLevel`: Add coin pile list for persistence
- `Hud.kt`: Add gold counter display
- `sounds/items/`: Convert MP3 files to OGG
