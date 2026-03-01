package kotlin2d

data class Chest(var x: Int, var y: Int, var opened: Boolean = false, val contents: List<ItemType>)
