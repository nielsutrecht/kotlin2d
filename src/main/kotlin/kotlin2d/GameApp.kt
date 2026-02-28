package kotlin2d

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

class GameApp(
    private val width: Int = 1280,
    private val height: Int = 720,
    private val title: String = "Kotlin2D LWJGL Game"
)
{
    private var window: Long = 0
    private var currentScene: Scene = FirstScene(width, height)

    fun run() {
        println("Running on LWJGL ${Version.getVersion()}")
        try {
            init()
            loop()
        } finally {
            cleanup()
        }
    }

    private fun init() {
        if (!glfwInit()) {
            error("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // Create the window
        window = glfwCreateWindow(width, height, title, 0, 0)
        if (window == 0L) {
            error("Failed to create GLFW window")
        }

        // Close on ESC
        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }
        }

        // Center the window on primary monitor
        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (videoMode != null) {
            val x = (videoMode.width() - width) / 2
            val y = (videoMode.height() - height) / 2
            glfwSetWindowPos(window, x, y)
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1) // V-sync
        glfwShowWindow(window)

        // Initialize OpenGL bindings
        GL.createCapabilities()

        glClearColor(0.1f, 0.1f, 0.2f, 1.0f)

        currentScene.onEnter()
    }

    private fun loop() {
        var lastTime = glfwGetTime().toFloat()

        while (!glfwWindowShouldClose(window)) {
            val now = glfwGetTime().toFloat()
            val delta = now - lastTime
            lastTime = now

            glfwPollEvents()

            currentScene.update(delta)
            currentScene.render()

            glfwSwapBuffers(window)
        }
    }

    private fun cleanup() {
        currentScene.onExit()

        if (window != 0L) {
            Callbacks.glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
        }

        glfwTerminate()
    }
}

