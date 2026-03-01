package kotlin2d

data class CachedLevel(
    val map: GameMap,
    val rooms: List<Room>,
    val items: MutableList<Item>,
    val enemies: MutableList<Enemy>,
    var playerX: Int,
    var playerY: Int
)

object GameState {

    val inventory = Inventory()
    var currentLevel = 1
    val dungeonCache = mutableMapOf<Int, CachedLevel>()

    var maxHp = 20
    var hp = 20
    var attack = 5
    var defense = 2

    fun reset() {
        inventory.clear()
        currentLevel = 1
        dungeonCache.clear()
        maxHp = 20
        hp = 20
        attack = 5
        defense = 2
    }
}
