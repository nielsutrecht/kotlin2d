package kotlin2d

class GameMap(val width: Int, val height: Int) {

    private val tiles = Array(height) { Array(width) { DungeonTileset.floor } }

    operator fun get(x: Int, y: Int): TileDef {
        if (x !in 0 until width || y !in 0 until height) return DungeonTileset.wall
        return tiles[y][x]
    }

    operator fun set(x: Int, y: Int, tile: TileDef) {
        if (x in 0 until width && y in 0 until height) {
            tiles[y][x] = tile
        }
    }

    fun isWalkable(x: Int, y: Int): Boolean {
        if (x !in 0 until width || y !in 0 until height) return false
        return this[x, y].kind != TileKind.WALL
    }

    fun fill(tile: TileDef) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                tiles[y][x] = tile
            }
        }
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int, tile: TileDef) {
        for (dy in y until (y + h).coerceAtMost(height)) {
            for (dx in x until (x + w).coerceAtMost(width)) {
                if (dx in 0 until width && dy in 0 until height) {
                    tiles[dy][dx] = tile
                }
            }
        }
    }
}
