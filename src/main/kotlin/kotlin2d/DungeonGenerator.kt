package kotlin2d

import kotlin.random.Random

data class Room(val x: Int, val y: Int, val w: Int, val h: Int) {
    val centerX get() = x + w / 2
    val centerY get() = y + h / 2

    fun randomFloor(random: Random): Pair<Int, Int> {
        return Pair(
            random.nextInt(x + 1, x + w - 1),
            random.nextInt(y + 1, y + h - 1)
        )
    }
}

data class GeneratedDungeon(
    val map: GameMap,
    val rooms: List<Room>,
    val items: MutableList<Item>,
    val enemies: MutableList<Enemy>,
    val chests: MutableList<Chest>,
    val coinPiles: MutableList<CoinPile>
)

object DungeonGenerator {

    fun generate(
        width: Int = 80,
        height: Int = 60,
        roomAttempts: Int = 30,
        minRoomSize: Int = 5,
        maxRoomSize: Int = 12,
        random: Random = Random,
        level: Int = 1
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

        // Place items
        val items = mutableListOf<Item>()

        // Place locked doors at some corridor chokepoints and keys before them
        val doorsPlaced = mutableSetOf<Int>()
        for (i in 1 until rooms.size) {
            if (random.nextFloat() < 0.3f && i !in doorsPlaced) {
                val prev = rooms[i - 1]
                val curr = rooms[i]
                val doorPos = findCorridorChokepoint(map, prev.centerX, prev.centerY, curr.centerX, curr.centerY)
                if (doorPos != null) {
                    map[doorPos.first, doorPos.second] = DungeonTileset.doorClosed
                    doorsPlaced.add(i)
                    // Place a key in a room before the door
                    val keyRoom = rooms[random.nextInt(0, i)]
                    val (kx, ky) = keyRoom.randomFloor(random)
                    if (map[kx, ky] == DungeonTileset.floor) {
                        items.add(Item(ItemType.KEY, kx, ky))
                    }
                }
            }
        }

        // Scatter potions in random rooms (skip first room)
        for (i in 1 until rooms.size) {
            if (random.nextFloat() < 0.35f) {
                val (px, py) = rooms[i].randomFloor(random)
                if (map[px, py] == DungeonTileset.floor) {
                    items.add(Item(ItemType.HEALTH_POTION, px, py))
                }
            }
        }

        // Spawn enemies in rooms (skip first room)
        val enemies = mutableListOf<Enemy>()
        val availableTypes = when {
            level >= 3 -> EnemyType.entries
            level >= 2 -> listOf(EnemyType.GREEN_SLIME, EnemyType.RAT, EnemyType.SKELETON, EnemyType.GOBLIN)
            else -> listOf(EnemyType.GREEN_SLIME, EnemyType.RAT)
        }
        val occupied = items.map { it.x to it.y }.toMutableSet()
        for (i in 1 until rooms.size) {
            if (random.nextFloat() < 0.6f) {
                val count = random.nextInt(1, 4)
                repeat(count) {
                    val (ex, ey) = rooms[i].randomFloor(random)
                    val pos = ex to ey
                    if (map[ex, ey] == DungeonTileset.floor && pos !in occupied) {
                        val type = availableTypes[random.nextInt(availableTypes.size)]
                        enemies.add(Enemy(type, ex, ey))
                        occupied.add(pos)
                    }
                }
            }
        }

        // Spawn chests (skip first room, max one per room, ~20% chance)
        val chests = mutableListOf<Chest>()
        for (i in 1 until rooms.size) {
            if (random.nextFloat() < 0.2f) {
                val (cx, cy) = rooms[i].randomFloor(random)
                val pos = cx to cy
                if (map[cx, cy] == DungeonTileset.floor && pos !in occupied) {
                    val allTypes = listOf(ItemType.KEY, ItemType.HEALTH_POTION, ItemType.SWORD)
                    val count = random.nextInt(0, 4) // 0-3
                    val contents = allTypes.shuffled(random).take(count)
                    chests.add(Chest(cx, cy, contents = contents))
                    occupied.add(pos)
                }
            }
        }

        return GeneratedDungeon(map, rooms, items, enemies, chests, mutableListOf())
    }

    private fun findCorridorChokepoint(map: GameMap, x1: Int, y1: Int, x2: Int, y2: Int): Pair<Int, Int>? {
        // Walk the L-shaped corridor and find a single-width point
        // Try the corner of the L-shape
        val cornerX = x2
        val cornerY = y1
        if (map[cornerX, cornerY] == DungeonTileset.floor) {
            // Check if it's a chokepoint (walls on two opposite sides)
            val horizBlocked = map[cornerX - 1, cornerY].kind == TileKind.WALL && map[cornerX + 1, cornerY].kind == TileKind.WALL
            val vertBlocked = map[cornerX, cornerY - 1].kind == TileKind.WALL && map[cornerX, cornerY + 1].kind == TileKind.WALL
            if (horizBlocked || vertBlocked) {
                return Pair(cornerX, cornerY)
            }
        }
        // Try a point along the horizontal segment
        val midX = (x1 + x2) / 2
        if (map[midX, y1] == DungeonTileset.floor) {
            val vertWalls = map[midX, y1 - 1].kind == TileKind.WALL && map[midX, y1 + 1].kind == TileKind.WALL
            if (vertWalls) return Pair(midX, y1)
        }
        // Try a point along the vertical segment
        val midY = (y1 + y2) / 2
        if (map[x2, midY] == DungeonTileset.floor) {
            val horizWalls = map[x2 - 1, midY].kind == TileKind.WALL && map[x2 + 1, midY].kind == TileKind.WALL
            if (horizWalls) return Pair(x2, midY)
        }
        return null
    }

    private fun carveCorridor(map: GameMap, x1: Int, y1: Int, x2: Int, y2: Int) {
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
