package com.example.beproducktive.utils

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

public class Converters {

    @TypeConverter
    public fun fromDate(deadline: Date?): String? =
        SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).format(deadline!!)

    @TypeConverter
    public fun stringToDate(value: String?): Date? =
        SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).parse(value!!)

    companion object {

        @TypeConverter
        fun fromDate(deadline: Date?): String? =
            SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).format(deadline!!)

        @TypeConverter
        fun stringToDate(value: String?): Date? =
            SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).parse(value!!)
    }
}