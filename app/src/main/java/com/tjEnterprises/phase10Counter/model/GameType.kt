package com.tjEnterprises.phase10Counter.model

import com.tjEnterprises.phase10Counter.R

enum class GameType(val key: String, val resourceId: Int) {
    GAME_TYPE_STANDARD("standard", R.string.gameTypeStandard),
    GAME_TYPE_FLIP("flip", R.string.gameTypeFlip);

    companion object {
        fun fromKey(key: String): GameType? {
            return entries.find { it.key == key }
        }
    }
}