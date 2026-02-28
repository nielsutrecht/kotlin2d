package kotlin2d

import kotlin.random.Random

data class Room(val x: Int, val y: Int, val w: Int, val h: Int) {
    val centerX get() = x + w / 2
    val centerY get() = y + h / 2
}

data class GeneratedDungeon(val map: GameMap, val rooms: List<Room>)

object DungeonGenerator {

    fun generate(
        width: Int = 80,
        height: Int = 60,
        roomAttempts: Int = 30,
        minRoomSize: Int = 5,
        maxRoomSize: Int = 12,
        random: Random = Random
    ): GeneratedDungeon {
        val map = GameMap(width, height)
        map.fill(DungeonTileset.wall)

        val rooms = mutableListOf<Room>()

        repeat(roomAttempts) {
            val rw = random.nextInt(minRoomSize, maxRoomSize + 1)
            val rh = random.nextInt(minRoomSize, maxRoomSize + 1)
            val rx = random.nextInt(1, width - rw - 1)
            val ry = random.nextInt(1, height - rh - 1)

            val candidate = Room(rx, ry, rw, rh)

            val overlaps = rooms.any { existing ->
                candidate.x - 1 < existing.x + existing.w &&
                candidate.x + candidate.w + 1 > existing.x &&
                candidate.y - 1 < existing.y + existing.h &&
                candidate.y + candidate.h + 1 > existing.y
            }

            if (!overlaps) {
                map.fillRect(candidate.x, candidate.y, candidate.w, candidate.h, DungeonTileset.floor)
                rooms.add(candidate)
            }
        }

        // Connect each room to the previous one via L-shaped corridors
        for (i in 1 until rooms.size) {
            val prev = rooms[i - 1]
            val curr = rooms[i]
            carveCorridor(map, prev.centerX, prev.centerY, curr.centerX, curr.centerY)
        }

        // Place stairs
        if (rooms.size >= 2) {
            map[rooms.first().centerX, rooms.first().centerY] = DungeonTileset.stairsUp
            map[rooms.last().centerX, rooms.last().centerY] = DungeonTileset.stairsDown
        }

        return GeneratedDungeon(map, rooms)
    }

    private fun carveCorridor(map: GameMap, x1: Int, y1: Int, x2: Int, y2: Int) {
        // Horizontal then vertical
        val xStep = if (x2 > x1) 1 else -1
        var x = x1
        while (x != x2) {
            map[x, y1] = DungeonTileset.floor
            x += xStep
        }
        val yStep = if (y2 > y1) 1 else -1
        var y = y1
        while (y != y2) {
            map[x2, y] = DungeonTileset.floor
            y += yStep
        }
        map[x2, y2] = DungeonTileset.floor
    }
}
