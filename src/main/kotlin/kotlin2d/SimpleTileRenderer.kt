package kotlin2d

import org.lwjgl.opengl.GL11.GL_QUADS
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBegin
import org.lwjgl.opengl.GL11.glColor4f
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.GL11.glTexCoord2f
import org.lwjgl.opengl.GL11.glVertex2f

/**
 * Helper for drawing textured 2D tiles as quads in normalized device
 * coordinates using the fixed-function pipeline and a tileset texture.
 */
class SimpleTileRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val tileSize: Int,
    private val tileset: Texture
) {

    // Atlas tile size in pixels (art grid size), independent of on-screen tileSize.
    private val atlasTileSize = 32

    init {
        glEnable(GL_TEXTURE_2D)
    }

    /**
     * Draws a single tile at [gridX], [gridY] using the given [tileDef]
     * from the tileset atlas.
     */
    fun drawTile(gridX: Int, gridY: Int, tileDef: TileDef) {
        tileset.bind()

        glColor4f(1f, 1f, 1f, 1f)

        val x0 = gridX * tileSize.toFloat()
        val y0 = gridY * tileSize.toFloat()
        val x1 = x0 + tileSize
        val y1 = y0 + tileSize

        fun toNdcX(px: Float): Float = (px / screenWidth.toFloat()) * 2f - 1f
        fun toNdcY(py: Float): Float = 1f - (py / screenHeight.toFloat()) * 2f

        val nx0 = toNdcX(x0)
        val ny0 = toNdcY(y0)
        val nx1 = toNdcX(x1)
        val ny1 = toNdcY(y1)

        val u0 = (tileDef.atlasX * atlasTileSize).toFloat() / tileset.width.toFloat()
        val u1 = ((tileDef.atlasX + 1) * atlasTileSize).toFloat() / tileset.width.toFloat()
        val v0 = (tileDef.atlasY * atlasTileSize).toFloat() / tileset.height.toFloat()
        val v1 = ((tileDef.atlasY + 1) * atlasTileSize).toFloat() / tileset.height.toFloat()

        glBegin(GL_QUADS)
        glTexCoord2f(u0, v0); glVertex2f(nx0, ny0)
        glTexCoord2f(u1, v0); glVertex2f(nx1, ny0)
        glTexCoord2f(u1, v1); glVertex2f(nx1, ny1)
        glTexCoord2f(u0, v1); glVertex2f(nx0, ny1)
        glEnd()
    }
}

