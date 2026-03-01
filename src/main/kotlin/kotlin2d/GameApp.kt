package kotlin2d

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

class GameApp(
    val width: Int = 1280,
    val height: Int = 720,
    private val title: String = "Kotlin2D LWJGL Game"
) {
    private var window: Long = 0
    private var currentScene: Scene? = null
    private var pendingScene: Scene? = null

    fun run() {
        println("Running on LWJGL ${Version.getVersion()}")
        try {
            init()
            loop()
        } finally {
            cleanup()
        }
    }

    fun requestSceneSwitch(scene: Scene) {
        pendingScene = scene
    }

    private fun init() {
        if (!glfwInit()) {
            error("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        window = glfwCreateWindow(width, height, title, 0, 0)
        if (window == 0L) {
            error("Failed to create GLFW window")
        }

        Input.window = window

        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }
        }

        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (videoMode != null) {
            val x = (videoMode.width() - width) / 2
            val y = (videoMode.height() - height) / 2
            glfwSetWindowPos(window, x, y)
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)

        GL.createCapabilities()

        glClearColor(0.1f, 0.1f, 0.2f, 1.0f)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        GameState.reset()
        Audio.init()
        requestSceneSwitch(DungeonScene(this, level = 1))
    }

    private fun loop() {
        var lastTime = glfwGetTime().toFloat()

        while (!glfwWindowShouldClose(window)) {
            // Handle scene switching at top of loop
            pendingScene?.let { next ->
                currentScene?.onExit()
                currentScene = next
                pendingScene = null
                next.onEnter()
            }

            val now = glfwGetTime().toFloat()
            val delta = now - lastTime
            lastTime = now

            glfwPollEvents()

            Audio.update()
            currentScene?.update(delta)
            currentScene?.render()

            glfwSwapBuffers(window)
        }
    }

    private fun cleanup() {
        Audio.cleanup()
        currentScene?.onExit()

        if (window != 0L) {
            Callbacks.glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
        }

        glfwTerminate()
    }
}
