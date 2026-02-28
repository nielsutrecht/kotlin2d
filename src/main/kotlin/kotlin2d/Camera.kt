package kotlin2d

class Camera(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val tileSize: Int
) {
    var pixelX: Float = 0f
        private set
    var pixelY: Float = 0f
        private set

    fun follow(targetGridX: Int, targetGridY: Int, mapWidth: Int, mapHeight: Int) {
        val targetPixelX = targetGridX * tileSize + tileSize / 2f
        val targetPixelY = targetGridY * tileSize + tileSize / 2f

        val halfW = screenWidth / 2f
        val halfH = screenHeight / 2f

        val mapPixelW = mapWidth * tileSize.toFloat()
        val mapPixelH = mapHeight * tileSize.toFloat()

        pixelX = (targetPixelX - halfW).coerceIn(0f, (mapPixelW - screenWidth).coerceAtLeast(0f))
        pixelY = (targetPixelY - halfH).coerceIn(0f, (mapPixelH - screenHeight).coerceAtLeast(0f))
    }

    fun visibleTileRange(mapWidth: Int, mapHeight: Int): VisibleRange {
        val startX = (pixelX / tileSize).toInt().coerceAtLeast(0)
        val startY = (pixelY / tileSize).toInt().coerceAtLeast(0)
        val endX = ((pixelX + screenWidth) / tileSize).toInt().coerceAtMost(mapWidth - 1)
        val endY = ((pixelY + screenHeight) / tileSize).toInt().coerceAtMost(mapHeight - 1)
        return VisibleRange(startX, startY, endX, endY)
    }

    data class VisibleRange(val startX: Int, val startY: Int, val endX: Int, val endY: Int)
}
