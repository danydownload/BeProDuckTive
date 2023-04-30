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

    // You can add companion object to Converters class to make the class accessible from other classes without creating an instance of it.

    companion object {

        @TypeConverter
        fun fromDate(deadline: Date?): String? =
            SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).format(deadline!!)

        @TypeConverter
        fun stringToDate(value: String?): Date? =
            SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).parse(value!!)
    }
}