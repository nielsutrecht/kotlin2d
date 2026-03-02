package kotlin2d

enum class EnemyState { IDLE, CHASING }

enum class EnemyType(val hp: Int, val attack: Int, val defense: Int, val detectionRange: Int, val turnsPerMove: Int) {
    GREEN_SLIME(hp = 5, attack = 2, defense = 0, detectionRange = 4, turnsPerMove = 2),
    RAT(hp = 4, attack = 3, defense = 0, detectionRange = 5, turnsPerMove = 1),
    SKELETON(hp = 10, attack = 5, defense = 2, detectionRange = 8, turnsPerMove = 1),
    GOBLIN(hp = 8, attack = 4, defense = 1, detectionRange = 7, turnsPerMove = 1),
    BAT(hp = 6, attack = 4, defense = 1, detectionRange = 6, turnsPerMove = 1)
}

data class Enemy(
    val type: EnemyType,
    var x: Int,
    var y: Int,
    var hp: Int = type.hp,
    val attack: Int = type.attack,
    val defense: Int = type.defense,
    var state: EnemyState = EnemyState.IDLE,
    val spawnRoom: Room? = null,
    var turnCounter: Int = 0
)
