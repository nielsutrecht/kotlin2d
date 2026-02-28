package kotlin2d

class Inventory {

    private val items = mutableListOf<ItemType>()

    val contents: List<ItemType> get() = items

    fun add(type: ItemType) {
        items.add(type)
    }

    fun has(type: ItemType): Boolean = type in items

    fun remove(type: ItemType): Boolean = items.remove(type)

    fun clear() {
        items.clear()
    }
}
