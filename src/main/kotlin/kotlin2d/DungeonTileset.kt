package kotlin2d

/**
 * Logical description of the dungeon tiles we can render from a sprite atlas.
 *
 * Each tile is assumed to be square in the **game world** (screen space),
 * using [TILE_SIZE] pixels per tile. The [atlasX]/[atlasY] coordinates refer
 * to tile positions in the atlas grid (not pixels) and are mapped to the
 * actual texture size by the renderer.
 *
 * The current atlas layout is a single row:
 * [0] floor     [1] wall     [2] door-closed  [3] door-open  [4] stairs-down  [5] stairs-up
 */

// How large a tile should appear on screen (in pixels).
// Our atlas uses 32x32 tiles, and we render them 1:1.
const val TILE_SIZE = 32

enum class TileKind {
    FLOOR,
    WALL,
    DOOR,
    STAIRS,
    ITEM,
    ENEMY,
    CHEST
}

enum class DoorVariant {
    CLOSED,
    OPEN
}

enum class StairsVariant {
    DOWN,
    UP
}

data class TileDef(
    val kind: TileKind,
    val variant: String = "",
    val atlasX: Int,
    val atlasY: Int
)

object DungeonTileset {

    // Base tiles
    val floor = TileDef(
        kind = TileKind.FLOOR,
        variant = "default",
        atlasX = 41,
        atlasY = 12
    )

    val wall = TileDef(
        kind = TileKind.WALL,
        variant = "default",
        atlasX = 26,   // 832 / 32
        atlasY = 16    // 512 / 32
    )

    val doorClosed = TileDef(
        kind = TileKind.DOOR,
        variant = DoorVariant.CLOSED.name.lowercase(),
        atlasX = 23,   // 736 / 32
        atlasY = 11    // 352 / 32
    )

    val doorOpen = TileDef(
        kind = TileKind.DOOR,
        variant = DoorVariant.OPEN.name.lowercase(),
        atlasX = 27,   // 864 / 32
        atlasY = 11    // 352 / 32
    )

    val stairsDown = TileDef(
        kind = TileKind.STAIRS,
        variant = StairsVariant.DOWN.name.lowercase(),
        atlasX = 41,   // 1312 / 32
        atlasY = 15    // 480 / 32
    )

    val stairsUp = TileDef(
        kind = TileKind.STAIRS,
        variant = StairsVariant.UP.name.lowercase(),
        atlasX = 42,   // 1344 / 32
        atlasY = 15    // 480 / 32
    )

    // Chests
    val chestClosed = TileDef(
        kind = TileKind.CHEST,
        variant = "closed",
        atlasX = 44,
        atlasY = 45
    )

    val chestOpen = TileDef(
        kind = TileKind.CHEST,
        variant = "open",
        atlasX = 45,
        atlasY = 45
    )

    // Items
    val key = TileDef(
        kind = TileKind.ITEM,
        variant = "key",
        atlasX = 54,
        atlasY = 45
    )

    val potion = TileDef(
        kind = TileKind.ITEM,
        variant = "potion",
        atlasX = 2,
        atlasY = 25
    )

    val sword = TileDef(
        kind = TileKind.ITEM,
        variant = "sword",
        atlasX = 4,
        atlasY = 29
    )

    // Enemies
    val greenSlime = TileDef(
        kind = TileKind.ENEMY,
        variant = "green_slime",
        atlasX = 49,
        atlasY = 5
    )

    val skeleton = TileDef(
        kind = TileKind.ENEMY,
        variant = "skeleton",
        atlasX = 22,
        atlasY = 8
    )

    val bat = TileDef(
        kind = TileKind.ENEMY,
        variant = "bat",
        atlasX = 44,
        atlasY = 3
    )

    val rat = TileDef(
        kind = TileKind.ENEMY,
        variant = "rat",
        atlasX = 64,
        atlasY = 3
    )

    val goblin = TileDef(
        kind = TileKind.ENEMY,
        variant = "goblin",
        atlasX = 45,
        atlasY = 2
    )

    // Player character
    val player = TileDef(
        kind = TileKind.FLOOR, // occupies floor tile
        variant = "player",
        atlasX = 4,
        atlasY = 2
    )

    val enemyTile: Map<EnemyType, TileDef> = mapOf(
        EnemyType.GREEN_SLIME to greenSlime,
        EnemyType.SKELETON to skeleton,
        EnemyType.BAT to bat,
        EnemyType.RAT to rat,
        EnemyType.GOBLIN to goblin
    )

    val all: List<TileDef> = listOf(
        floor,
        wall,
        doorClosed,
        doorOpen,
        stairsDown,
        stairsUp,
        chestClosed,
        chestOpen,
        key,
        potion,
        sword,
        greenSlime,
        skeleton,
        bat,
        rat,
        goblin,
        player
    )

    val byKind: Map<TileKind, List<TileDef>> = all.groupBy { it.kind }
}

