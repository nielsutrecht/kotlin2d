package kotlin2d

/**
 * Logical description of the dungeon tiles we can render from a sprite atlas.
 *
 * Each tile is assumed to be a square of [TILE_SIZE] pixels. The [atlasX]/[atlasY]
 * coordinates refer to tile positions in the atlas grid (not pixels):
 * - pixelX = atlasX * TILE_SIZE
 * - pixelY = atlasY * TILE_SIZE
 *
 * The initial atlas layout we target is a single row:
 * [0] floor     [1] wall     [2] door-closed  [3] door-open  [4] stairs-down  [5] stairs-up
 */

const val TILE_SIZE = 16

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
        atlasX = 0,
        atlasY = 0
    )

    val wall = TileDef(
        kind = TileKind.WALL,
        variant = "default",
        atlasX = 1,
        atlasY = 0
    )

    val doorClosed = TileDef(
        kind = TileKind.DOOR,
        variant = DoorVariant.CLOSED.name.lowercase(),
        atlasX = 2,
        atlasY = 0
    )

    val doorOpen = TileDef(
        kind = TileKind.DOOR,
        variant = DoorVariant.OPEN.name.lowercase(),
        atlasX = 3,
        atlasY = 0
    )

    val stairsDown = TileDef(
        kind = TileKind.STAIRS,
        variant = StairsVariant.DOWN.name.lowercase(),
        atlasX = 4,
        atlasY = 0
    )

    val stairsUp = TileDef(
        kind = TileKind.STAIRS,
        variant = StairsVariant.UP.name.lowercase(),
        atlasX = 5,
        atlasY = 0
    )

    val all: List<TileDef> = listOf(
        floor,
        wall,
        doorClosed,
        doorOpen,
        stairsDown,
        stairsUp
    )

    val byKind: Map<TileKind, List<TileDef>> = all.groupBy { it.kind }
}

