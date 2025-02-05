package com.tjEnterprises.phase10Counter.data.legacy

import androidx.room.TypeConverter
import java.util.Date

/**
 * Class with several type converter methods for converting different types of timestamps.
 * Used for saving timestamps in database.
 */
class Converters {
    /**
     * Converts a timestamp given as number of milliseconds into a Date object.
     * Returns null if Long containing number of milliseconds is null.
     *
     * @param value number of milliseconds to convert into date object
     * @return date object representing same timestamp as number of milliseconds
     */
    @Deprecated(message = "Deprecated, should only be used for backwards compatibility.")
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a Date object into a Long with number of milliseconds.
     * If the Date object or its time attribute is null, null is returned.
     *
     * @param date the Date object to be converted into number of milliseconds
     * @return number of milliseconds representing the Date object or null if Date object is null
     */
    @Deprecated(message = "Deprecated, should only be used for backwards compatibility.")
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}