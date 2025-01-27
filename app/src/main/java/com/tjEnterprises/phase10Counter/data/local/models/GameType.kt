package com.tjEnterprises.phase10Counter.data.local.models

import com.tjEnterprises.phase10Counter.R


object GameType {

    interface Type {
        val key: String
        val resourceId: Int
    }

    const val DEFAULT_GAMETYPE_KEY = "standard"

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

}
