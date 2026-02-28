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
    STAIRS
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

    // Player character
    val player = TileDef(
        kind = TileKind.FLOOR, // occupies floor tile
        variant = "player",
        atlasX = 4,
        atlasY = 2
    )

    val all: List<TileDef> = listOf(
        floor,
        wall,
        doorClosed,
        doorOpen,
        stairsDown,
        stairsUp,
        player
    )

    val byKind: Map<TileKind, List<TileDef>> = all.groupBy { it.kind }
}

