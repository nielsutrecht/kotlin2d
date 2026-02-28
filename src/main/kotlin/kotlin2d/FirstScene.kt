package kotlin2d

import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.glfw.GLFW.*

/**
 * First real scene: renders a simple dungeon layout using the logical tileset
 * and the dungeon tileset texture.
 */
class FirstScene(
    private val screenWidth: Int,
    private val screenHeight: Int
) : Scene {

    private lateinit var renderer: SimpleTileRenderer
    private lateinit var tiles: Array<Array<TileDef>>
    private lateinit var tilesetTexture: Texture

    private var playerX: Int = 0
    private var playerY: Int = 0
    private var moveCooldown = 0.15f
    private var moveTimer = 0f

    override fun onEnter() {
        glClearColor(0f, 0f, 0f, 1f)

        tilesetTexture = Texture.load("/textures/dungeon-tileset.png")
        renderer = SimpleTileRenderer(screenWidth, screenHeight, TILE_SIZE, tilesetTexture)
        tiles = createDungeonLayout()

        playerX = screenWidth / (2 * TILE_SIZE)
        playerY = screenHeight / (2 * TILE_SIZE)
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

        if (dx == 0 && dy == 0) {
            return
        }

        val targetX = playerX + dx
        val targetY = playerY + dy

        if (targetY in tiles.indices && targetX in tiles[0].indices) {
            val targetTile = tiles[targetY][targetX]
            if (targetTile.kind != TileKind.WALL) {
                playerX = targetX
                playerY = targetY
            }
        }

        moveTimer = 0f
    }

    override fun render() {
        glClear(GL_COLOR_BUFFER_BIT)

        for (y in tiles.indices) {
            val row = tiles[y]
            for (x in row.indices) {
                renderer.drawTile(x, y, row[x])
            }
        }

        renderer.drawTile(playerX, playerY, DungeonTileset.player)
    }

    private fun createDungeonLayout(): Array<Array<TileDef>> {
        val tilesWide = screenWidth / TILE_SIZE
        val tilesHigh = screenHeight / TILE_SIZE

        val grid = Array(tilesHigh) { Array(tilesWide) { DungeonTileset.floor } }

        for (y in 0 until tilesHigh) {
            for (x in 0 until tilesWide) {
                val isBorder = x == 0 || y == 0 || x == tilesWide - 1 || y == tilesHigh - 1
                if (isBorder) {
                    grid[y][x] = DungeonTileset.wall
                }
            }
        }

        val centerX = tilesWide / 2
        val bottomY = tilesHigh - 1
        grid[bottomY][centerX] = DungeonTileset.doorClosed

        val midY = tilesHigh / 2
        grid[midY][tilesWide / 4] = DungeonTileset.stairsDown
        grid[midY][3 * tilesWide / 4] = DungeonTileset.stairsUp

        return grid
    }
}

