package kotlin2d

import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor

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

    override fun onEnter() {
        glClearColor(0f, 0f, 0f, 1f)

        tilesetTexture = Texture.load("/textures/dungeon-tileset.png")
        renderer = SimpleTileRenderer(screenWidth, screenHeight, TILE_SIZE, tilesetTexture)
        tiles = createDungeonLayout()
    }

    override fun update(delta: Float) {
        // No animation in this scene yet; purely static dungeon view.
    }

    override fun render() {
        glClear(GL_COLOR_BUFFER_BIT)

        for (y in tiles.indices) {
            val row = tiles[y]
            for (x in row.indices) {
                renderer.drawTile(x, y, row[x])
            }
        }
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

