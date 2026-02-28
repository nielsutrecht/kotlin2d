package kotlin2d

interface Scene {
    fun update(delta: Float)
    fun render()
    fun onEnter() {}
    fun onExit() {}
}

