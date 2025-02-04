package com.tjEnterprises.phase10Counter.data.legacy

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converter class for converting different types of timestamps.
 */
class Converters {
    /**
     * Converts a timestamp given as number of milliseconds into a Date object.
     * Returns null if Long containing number of milliseconds is null.
     *
     * @param value number of milliseconds to convert into date object
     * @return date object representing same timestamp as number of milliseconds
     */
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
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}