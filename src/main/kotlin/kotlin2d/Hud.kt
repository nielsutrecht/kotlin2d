package kotlin2d

class Hud(private val renderer: SimpleTileRenderer, private val screenWidth: Int) {

    fun render(level: Int) {
        // Draw level indicator using stairs tile at top-left
        renderer.drawScreenTile(4, 4, DungeonTileset.stairsDown)

        // Draw inventory items starting from x=80
        var offsetX = 80
        for (itemType in GameState.inventory.contents) {
            val tileDef = when (itemType) {
                ItemType.KEY -> DungeonTileset.key
                ItemType.HEALTH_POTION -> DungeonTileset.potion
                ItemType.SWORD -> DungeonTileset.sword
            }
            renderer.drawScreenTile(offsetX, 4, tileDef)
            offsetX += TILE_SIZE + 4
        }
    }
}
