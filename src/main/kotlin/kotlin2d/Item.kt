package kotlin2d

enum class ItemType {
    KEY,
    HEALTH_POTION,
    SWORD
}

data class Item(val type: ItemType, var x: Int, var y: Int)
