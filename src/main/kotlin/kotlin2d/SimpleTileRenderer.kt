package kotlin2d

import org.lwjgl.opengl.GL11.GL_QUADS
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBegin
import org.lwjgl.opengl.GL11.glColor4f
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.GL11.glTexCoord2f
import org.lwjgl.opengl.GL11.glVertex2f

class SimpleTileRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val tileSize: Int,
    private val tileset: Texture
) {

    private val atlasTileSize = 32

    var cameraX: Float = 0f
    var cameraY: Float = 0f

    init {
        glEnable(GL_TEXTURE_2D)
    }

    fun begin() {
        tileset.bind()
        glColor4f(1f, 1f, 1f, 1f)
        glBegin(GL_QUADS)
    }

    fun end() {
        glEnd()
    }

    fun drawTileBatched(gridX: Int, gridY: Int, tileDef: TileDef) {
        val x0 = gridX * tileSize - cameraX
        val y0 = gridY * tileSize - cameraY
        val x1 = x0 + tileSize
        val y1 = y0 + tileSize

        // Cull off-screen tiles
        if (x1 < 0 || x0 > screenWidth || y1 < 0 || y0 > screenHeight) return

        val nx0 = (x0 / screenWidth) * 2f - 1f
        val ny0 = 1f - (y0 / screenHeight) * 2f
        val nx1 = (x1 / screenWidth) * 2f - 1f
        val ny1 = 1f - (y1 / screenHeight) * 2f

        val u0 = (tileDef.atlasX * atlasTileSize).toFloat() / tileset.width
        val u1 = ((tileDef.atlasX + 1) * atlasTileSize).toFloat() / tileset.width
        val v0 = (tileDef.atlasY * atlasTileSize).toFloat() / tileset.height
        val v1 = ((tileDef.atlasY + 1) * atlasTileSize).toFloat() / tileset.height

        glTexCoord2f(u0, v0); glVertex2f(nx0, ny0)
        glTexCoord2f(u1, v0); glVertex2f(nx1, ny0)
        glTexCoord2f(u1, v1); glVertex2f(nx1, ny1)
        glTexCoord2f(u0, v1); glVertex2f(nx0, ny1)
    }

    fun drawTile(gridX: Int, gridY: Int, tileDef: TileDef) {
        tileset.bind()
        glColor4f(1f, 1f, 1f, 1f)

        val x0 = gridX * tileSize - cameraX
        val y0 = gridY * tileSize - cameraY
        val x1 = x0 + tileSize
        val y1 = y0 + tileSize

        if (x1 < 0 || x0 > screenWidth || y1 < 0 || y0 > screenHeight) return

        val nx0 = (x0 / screenWidth) * 2f - 1f
        val ny0 = 1f - (y0 / screenHeight) * 2f
        val nx1 = (x1 / screenWidth) * 2f - 1f
        val ny1 = 1f - (y1 / screenHeight) * 2f

        val u0 = (tileDef.atlasX * atlasTileSize).toFloat() / tileset.width
        val u1 = ((tileDef.atlasX + 1) * atlasTileSize).toFloat() / tileset.width
        val v0 = (tileDef.atlasY * atlasTileSize).toFloat() / tileset.height
        val v1 = ((tileDef.atlasY + 1) * atlasTileSize).toFloat() / tileset.height

        glBegin(GL_QUADS)
        glTexCoord2f(u0, v0); glVertex2f(nx0, ny0)
        glTexCoord2f(u1, v0); glVertex2f(nx1, ny0)
        glTexCoord2f(u1, v1); glVertex2f(nx1, ny1)
        glTexCoord2f(u0, v1); glVertex2f(nx0, ny1)
        glEnd()
    }

    fun drawScreenTile(screenX: Int, screenY: Int, tileDef: TileDef) {
        tileset.bind()
        glColor4f(1f, 1f, 1f, 1f)

        val x0 = screenX.toFloat()
        val y0 = screenY.toFloat()
        val x1 = x0 + tileSize
        val y1 = y0 + tileSize

        val nx0 = (x0 / screenWidth) * 2f - 1f
        val ny0 = 1f - (y0 / screenHeight) * 2f
        val nx1 = (x1 / screenWidth) * 2f - 1f
        val ny1 = 1f - (y1 / screenHeight) * 2f

        val u0 = (tileDef.atlasX * atlasTileSize).toFloat() / tileset.width
        val u1 = ((tileDef.atlasX + 1) * atlasTileSize).toFloat() / tileset.width
        val v0 = (tileDef.atlasY * atlasTileSize).toFloat() / tileset.height
        val v1 = ((tileDef.atlasY + 1) * atlasTileSize).toFloat() / tileset.height

        glBegin(GL_QUADS)
        glTexCoord2f(u0, v0); glVertex2f(nx0, ny0)
        glTexCoord2f(u1, v0); glVertex2f(nx1, ny0)
        glTexCoord2f(u1, v1); glVertex2f(nx1, ny1)
        glTexCoord2f(u0, v1); glVertex2f(nx0, ny1)
        glEnd()
    }
}
