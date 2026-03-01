package kotlin2d

import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.glfw.GLFW.*

class DungeonScene(
    private val app: GameApp,
    private val level: Int = 1
) : Scene {

    private lateinit var renderer: SimpleTileRenderer
    private lateinit var map: GameMap
    private lateinit var camera: Camera
    private lateinit var tilesetTexture: Texture
    private lateinit var rooms: List<Room>
    private lateinit var hud: Hud
    private var items = mutableListOf<Item>()
    private var enemies = mutableListOf<Enemy>()
    private var chests = mutableListOf<Chest>()
    private var coinPiles = mutableListOf<CoinPile>()

    private var playerX: Int = 0
    private var playerY: Int = 0
    private var moveCooldown = 0.15f
    private var moveTimer = 0f

    override fun onEnter() {
        glClearColor(0f, 0f, 0f, 1f)

        tilesetTexture = Texture.load("/textures/dungeon-tileset.png")
        renderer = SimpleTileRenderer(app.width, app.height, TILE_SIZE, tilesetTexture)
        camera = Camera(app.width, app.height, TILE_SIZE)
        hud = Hud(renderer, app.width)

        GameState.currentLevel = level

        val cached = GameState.dungeonCache[level]
        if (cached != null) {
            // Restore from cache
            map = cached.map
            rooms = cached.rooms
            items = cached.items
            enemies = cached.enemies
            chests = cached.chests
            coinPiles = cached.coinPiles
            playerX = cached.playerX
            playerY = cached.playerY
        } else {
            // Generate new dungeon
            val dungeon = DungeonGenerator.generate(level = level)
            map = dungeon.map
            rooms = dungeon.rooms
            items = dungeon.items
            enemies = dungeon.enemies
            chests = dungeon.chests
            coinPiles = dungeon.coinPiles

            if (rooms.isNotEmpty()) {
                playerX = rooms.first().centerX
                playerY = rooms.first().centerY
            }

            // Cache it
            GameState.dungeonCache[level] = CachedLevel(map, rooms, items, enemies, chests, coinPiles, playerX, playerY)
        }
    }

    override fun onExit() {
        // Save player position for backtracking
        GameState.dungeonCache[level]?.let {
            it.playerX = playerX
            it.playerY = playerY
        }
    }

    override fun update(delta: Float) {
        moveTimer += delta
        if (moveTimer < moveCooldown) return

        var dx = 0
        var dy = 0

        if (glfwGetKey(Input.window, GLFW_KEY_W) == GLFW_PRESS || glfwGetKey(Input.window, GLFW_KEY_UP) == GLFW_PRESS) {
            dy = -1
        } else if (glfwGetKey(Input.window, GLFW_KEY_S) == GLFW_PRESS || glfwGetKey(Input.window, GLFW_KEY_DOWN) == GLFW_PRESS) {
            dy = 1
        }

        if (glfwGetKey(Input.window, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(Input.window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            dx = -1
        } else if (glfwGetKey(Input.window, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(Input.window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            dx = 1
        }

        if (dx == 0 && dy == 0) return

        val targetX = playerX + dx
        val targetY = playerY + dy
        val targetTile = map[targetX, targetY]

        // Door interaction
        if (targetTile == DungeonTileset.doorClosed) {
            if (GameState.inventory.has(ItemType.KEY)) {
                GameState.inventory.remove(ItemType.KEY)
                map[targetX, targetY] = DungeonTileset.doorOpen
                Audio.playSound("dungeon/door-open")
                // Move through the now-open door
                playerX = targetX
                playerY = targetY
            }
            // Blocked if no key
            moveTimer = 0f
            return
        }

        // Chest interaction
        val chest = chests.find { it.x == targetX && it.y == targetY }
        if (chest != null) {
            if (!chest.opened) {
                chest.opened = true
                Audio.playSound("dungeon/chest-open")

                // BFS to find free floor tiles for scatter
                val freeTiles = findScatterTiles(chest.x, chest.y, chest.contents.size + 1)

                // Scatter item contents
                for ((index, itemType) in chest.contents.withIndex()) {
                    if (index < freeTiles.size) {
                        items.add(Item(itemType, freeTiles[index].first, freeTiles[index].second))
                    }
                }

                // Drop coin pile
                val coinAmount = (0..1000).random()
                if (coinAmount > 0) {
                    val coinIdx = chest.contents.size
                    if (coinIdx < freeTiles.size) {
                        coinPiles.add(CoinPile(freeTiles[coinIdx].first, freeTiles[coinIdx].second, coinAmount))
                        Audio.playSound("items/coin-drop")
                    }
                }
            }
            moveTimer = 0f
            return
        }

        if (map.isWalkable(targetX, targetY)) {
            playerX = targetX
            playerY = targetY
            Audio.playSound("dungeon/walk-stone")

            // Pick up items
            val item = items.find { it.x == playerX && it.y == playerY }
            if (item != null) {
                GameState.inventory.add(item.type)
                items.remove(item)
                val soundName = when (item.type) {
                    ItemType.KEY -> "key"
                    ItemType.HEALTH_POTION -> "potion"
                    ItemType.SWORD -> "sword"
                }
                Audio.playSound(soundName)
            }

            // Pick up coin piles
            val pile = coinPiles.find { it.x == playerX && it.y == playerY }
            if (pile != null) {
                GameState.gold += pile.amount
                coinPiles.remove(pile)
                Audio.playSound("items/coin-take")
            }

            // Check stairs
            val tile = map[playerX, playerY]
            if (tile == DungeonTileset.stairsDown) {
                app.requestSceneSwitch(DungeonScene(app, level + 1))
            } else if (tile == DungeonTileset.stairsUp && level > 1) {
                app.requestSceneSwitch(DungeonScene(app, level - 1))
            }
        }

        moveTimer = 0f
    }

    override fun render() {
        glClear(GL_COLOR_BUFFER_BIT)

        camera.follow(playerX, playerY, map.width, map.height)
        renderer.cameraX = camera.pixelX
        renderer.cameraY = camera.pixelY

        val range = camera.visibleTileRange(map.width, map.height)

        renderer.begin()
        for (y in range.startY..range.endY) {
            for (x in range.startX..range.endX) {
                renderer.drawTileBatched(x, y, map[x, y])
            }
        }

        // Draw chests on the map
        for (chest in chests) {
            if (chest.x in range.startX..range.endX && chest.y in range.startY..range.endY) {
                val tileDef = if (chest.opened) DungeonTileset.chestOpen else DungeonTileset.chestClosed
                renderer.drawTileBatched(chest.x, chest.y, tileDef)
            }
        }

        // Draw coin piles on the map
        for (pile in coinPiles) {
            if (pile.x in range.startX..range.endX && pile.y in range.startY..range.endY) {
                renderer.drawTileBatched(pile.x, pile.y, DungeonTileset.coinPile)
            }
        }

        // Draw items on the map
        for (item in items) {
            if (item.x in range.startX..range.endX && item.y in range.startY..range.endY) {
                val tileDef = when (item.type) {
                    ItemType.KEY -> DungeonTileset.key
                    ItemType.HEALTH_POTION -> DungeonTileset.potion
                    ItemType.SWORD -> DungeonTileset.sword
                }
                renderer.drawTileBatched(item.x, item.y, tileDef)
            }
        }

        // Draw enemies on the map
        for (enemy in enemies) {
            if (enemy.x in range.startX..range.endX && enemy.y in range.startY..range.endY) {
                renderer.drawTileBatched(enemy.x, enemy.y, DungeonTileset.enemyTile.getValue(enemy.type))
            }
        }

        renderer.drawTileBatched(playerX, playerY, DungeonTileset.player)
        renderer.end()

        // HUD (screen-space, after world rendering)
        hud.render(level)
    }

    private fun findScatterTiles(originX: Int, originY: Int, count: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val claimed = mutableSetOf<Pair<Int, Int>>()
        val visited = mutableSetOf(originX to originY)
        val queue = ArrayDeque<Pair<Int, Int>>()

        // Seed with all 8 neighbors
        val dirs = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        for ((ddx, ddy) in dirs) {
            val pos = (originX + ddx) to (originY + ddy)
            if (pos !in visited) {
                visited.add(pos)
                queue.add(pos)
            }
        }

        val occupied = buildSet {
            addAll(items.map { it.x to it.y })
            addAll(enemies.map { it.x to it.y })
            addAll(chests.map { it.x to it.y })
            addAll(coinPiles.map { it.x to it.y })
            add(playerX to playerY)
        }

        while (result.size < count && queue.isNotEmpty()) {
            val (cx, cy) = queue.removeFirst()
            if (map.isWalkable(cx, cy) && (cx to cy) !in occupied && (cx to cy) !in claimed) {
                result.add(cx to cy)
                claimed.add(cx to cy)
            }
            // Expand
            for ((ddx, ddy) in dirs) {
                val next = (cx + ddx) to (cy + ddy)
                if (next !in visited) {
                    visited.add(next)
                    queue.add(next)
                }
            }
        }

        return result
    }
}
