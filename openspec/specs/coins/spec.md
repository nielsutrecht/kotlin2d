## ADDED Requirements

### Requirement: CoinPile entity tracks position and amount
A coin pile SHALL be represented as an entity with x/y position and an integer amount. Coin piles SHALL be stored as a mutable list alongside items, enemies, and chests in the dungeon data.

#### Scenario: CoinPile data structure
- **WHEN** a coin pile is created at position (7, 3) with amount 250
- **THEN** the coin pile SHALL have x=7, y=3, and amount=250

### Requirement: Coin pile has a tile definition
The tileset SHALL define `coinPile` at atlas coordinates (59, 23) with `TileKind.ITEM`.

#### Scenario: Coin pile rendering
- **WHEN** a coin pile is in the visible range
- **THEN** it SHALL be rendered using the `coinPile` tile definition

### Requirement: Player picks up coin piles by walking onto them
When the player moves onto a tile containing a coin pile, the pile's amount SHALL be added to `GameState.gold`, the `items/coin-take` sound SHALL play, and the pile SHALL be removed.

#### Scenario: Pick up a coin pile
- **WHEN** the player walks onto a tile containing a coin pile with amount 500
- **THEN** `GameState.gold` SHALL increase by 500, the `items/coin-take` sound SHALL play, and the coin pile SHALL be removed from the floor

### Requirement: GameState tracks player gold
`GameState` SHALL have a `gold: Int` field starting at 0. It SHALL be reset to 0 when `GameState.reset()` is called.

#### Scenario: Gold accumulates
- **WHEN** the player picks up piles of 100 and 250 gold
- **THEN** `GameState.gold` SHALL be 350

#### Scenario: Gold resets on new game
- **WHEN** `GameState.reset()` is called
- **THEN** `GameState.gold` SHALL be 0

### Requirement: Chests drop a coin pile when opened
When a chest is opened, a single coin pile with a random amount between 0 and 1000 SHALL be created and placed on a free floor tile near the chest alongside the item drops. The `items/coin-drop` sound SHALL play when the pile is placed. If the amount is 0, no coin pile SHALL be created.

#### Scenario: Chest drops coins
- **WHEN** a chest opens and the random coin amount is 750
- **THEN** a coin pile with amount 750 SHALL appear on a free floor tile near the chest and the `items/coin-drop` sound SHALL play

#### Scenario: Chest rolls zero coins
- **WHEN** a chest opens and the random coin amount is 0
- **THEN** no coin pile SHALL be created

### Requirement: Coin piles persist in level cache
Coin piles SHALL be included in `CachedLevel` so they persist when the player leaves and returns to a level.

#### Scenario: Return to level with coin piles
- **WHEN** a chest is opened on level 1 creating a coin pile, the player descends to level 2, then returns to level 1
- **THEN** the coin pile SHALL still be on the floor if not yet picked up

### Requirement: HUD displays gold counter
The HUD SHALL display the player's current gold total using a coin icon and the numeric amount.

#### Scenario: Gold shown on HUD
- **WHEN** the player has 1500 gold
- **THEN** the HUD SHALL display a coin icon with the number 1500

### Requirement: Coin sound effects are in OGG format
The sound files `sounds/items/coin-drop.mp3`, `sounds/items/coin-take-1.mp3`, and `sounds/items/coin-take-2.mp3` SHALL be converted to OGG format and the MP3 originals deleted.

#### Scenario: Sound files converted
- **WHEN** the game starts
- **THEN** `items/coin-drop` SHALL be available as a singleton sound and `items/coin-take` SHALL be available as a sound group with 2 variants
