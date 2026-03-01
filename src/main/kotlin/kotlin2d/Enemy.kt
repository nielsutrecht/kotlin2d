package kotlin2d

enum class EnemyType(val hp: Int, val attack: Int, val defense: Int) {
    GREEN_SLIME(hp = 5, attack = 2, defense = 0),
    RAT(hp = 4, attack = 3, defense = 0),
    SKELETON(hp = 10, attack = 5, defense = 2),
    GOBLIN(hp = 8, attack = 4, defense = 1),
    BAT(hp = 6, attack = 4, defense = 1)
}

data class Enemy(val type: EnemyType, var x: Int, var y: Int,
                 var hp: Int = type.hp, val attack: Int = type.attack, val defense: Int = type.defense)
