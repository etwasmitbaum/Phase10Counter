package com.tjEnterprises.phase10Counter.data.local.models

import com.tjEnterprises.phase10Counter.R


object GameType {

    // This constant value represents the default game type to be selected. It must be available at compile time
    const val DEFAULT_GAMETYPE_KEY = "standard"

    // This value represents the default GameType as the Type interface. This is simply a shorthand
    val defaultGameType: Type = getGameTypeByKey(DEFAULT_GAMETYPE_KEY)

    // This value hold all possible GameTypes in a single array
    val availableGameTypes = arrayOf(Standard, Flip, Masters)

    fun getGameTypeByKey(key: String): GameType.Type {
        val type = when (key) {
            Standard.key -> Standard
            Flip.key -> Flip
            Masters.key -> Masters
            else -> Invalid
        }
        return type
    }

    interface Type {
        val key: String
        val resourceId: Int
    }

    data object Standard : Type {
        override val key: String = DEFAULT_GAMETYPE_KEY
        override val resourceId: Int = R.string.gameTypeStandard
    }

    data object Flip : Type {
        override val key = "flip"
        override val resourceId: Int = R.string.gameTypeFlip
    }

    data object Masters : Type {
        override val key = "masters"
        override val resourceId: Int = R.string.gameTypeMasters
    }

    data object Invalid : Type {
        override val key = "INVALID"
        override val resourceId: Int = -1
    }

}
