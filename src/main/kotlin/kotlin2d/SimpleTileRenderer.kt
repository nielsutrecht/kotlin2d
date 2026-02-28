package kotlin2d

import org.lwjgl.opengl.GL11.GL_QUADS
import org.lwjgl.opengl.GL11.glBegin
import org.lwjgl.opengl.GL11.glColor3f
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.GL11.glVertex2f

/**
 * Very small helper for drawing colored 2D tiles as quads in normalized
 * device coordinates using the fixed-function pipeline.
 *
 * This keeps things simple and works with older OpenGL versions.
 */
class SimpleTileRenderer(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val tileSize: Int
) {

    /**
     * Draws a single tile at [gridX], [gridY] in tile coordinates (origin at top-left),
     * tinted by the given RGB color in [0,1].
     */
    fun drawTile(gridX: Int, gridY: Int, r: Float, g: Float, b: Float) {
        glColor3f(r, g, b)

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

        glBegin(GL_QUADS)
        glVertex2f(nx0, ny0)
        glVertex2f(nx1, ny0)
        glVertex2f(nx1, ny1)
        glVertex2f(nx0, ny1)
        glEnd()
    }
}

