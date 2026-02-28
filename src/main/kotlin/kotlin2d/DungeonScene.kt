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

    private var playerX: Int = 0
    private var playerY: Int = 0
    private var moveCooldown = 0.15f
    private var moveTimer = 0f

    override fun onEnter() {
        glClearColor(0f, 0f, 0f, 1f)

        tilesetTexture = Texture.load("/textures/dungeon-tileset.png")
        renderer = SimpleTileRenderer(app.width, app.height, TILE_SIZE, tilesetTexture)
        camera = Camera(app.width, app.height, TILE_SIZE)

        val dungeon = DungeonGenerator.generate()
        map = dungeon.map
        rooms = dungeon.rooms

        // Spawn player in first room's center
        if (rooms.isNotEmpty()) {
            playerX = rooms.first().centerX
            playerY = rooms.first().centerY
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

        if (map.isWalkable(targetX, targetY)) {
            playerX = targetX
            playerY = targetY

            // Check for stairs down
            val tile = map[playerX, playerY]
            if (tile == DungeonTileset.stairsDown) {
                app.requestSceneSwitch(DungeonScene(app, level + 1))
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
        renderer.drawTileBatched(playerX, playerY, DungeonTileset.player)
        renderer.end()
    }
}
