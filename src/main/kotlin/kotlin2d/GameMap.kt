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

    fun bfsDistanceMap(sourceX: Int, sourceY: Int, blocked: Set<Pair<Int, Int>> = emptySet()): IntArray {
        val dist = IntArray(width * height) { Int.MAX_VALUE }
        dist[sourceY * width + sourceX] = 0
        val queue = ArrayDeque<Int>()
        queue.add(sourceY * width + sourceX)
        val dirs = intArrayOf(0, -1, 0, 1, -1, 0, 1, 0) // dx,dy pairs
        while (queue.isNotEmpty()) {
            val idx = queue.removeFirst()
            val cx = idx % width
            val cy = idx / width
            val nd = dist[idx] + 1
            for (d in 0 until 8 step 2) {
                val nx = cx + dirs[d]
                val ny = cy + dirs[d + 1]
                if (nx !in 0 until width || ny !in 0 until height) continue
                val ni = ny * width + nx
                if (dist[ni] <= nd) continue
                val tile = tiles[ny][nx]
                if (tile.kind == TileKind.WALL || tile == DungeonTileset.doorClosed) continue
                if ((nx to ny) in blocked) continue
                dist[ni] = nd
                queue.add(ni)
            }
        }
        return dist
    }

    fun hasLineOfSight(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        var dx = kotlin.math.abs(x2 - x1)
        var dy = kotlin.math.abs(y2 - y1)
        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1
        var err = dx - dy
        var cx = x1
        var cy = y1
        while (cx != x2 || cy != y2) {
            val e2 = 2 * err
            if (e2 > -dy) { err -= dy; cx += sx }
            if (e2 < dx) { err += dx; cy += sy }
            if (cx == x2 && cy == y2) break
            if (this[cx, cy].kind == TileKind.WALL) return false
        }
        return true
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
