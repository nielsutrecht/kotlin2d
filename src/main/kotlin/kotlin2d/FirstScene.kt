package kotlin2d

import org.lwjgl.opengl.GL11.*

/**
 * A very simple first scene that just animates the clear color over time.
 * This avoids compatibility issues with deprecated immediate-mode drawing,
 * while still giving visible animation.
 */
class FirstScene : Scene {

    private var time = 0f

    override fun onEnter() {
        glClearColor(0.1f, 0.1f, 0.2f, 1.0f)
    }

    override fun update(delta: Float) {
        time += delta
    }

    override fun render() {
        val green = ((kotlin.math.sin(time.toDouble()) * 0.5) + 0.5).toFloat()
        val blue = ((kotlin.math.cos(time.toDouble()) * 0.5) + 0.5).toFloat()

        glClearColor(0.1f, green, blue, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
}

