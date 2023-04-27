package com.example.beproducktive.data.calendar

import com.example.beproducktive.utils.Converters
import java.text.SimpleDateFormat
import java.util.*

data class MyCalendar(
    var day: String? = null,
    var date: String? = null,
    var month: String? = null,
    var year: String? = null,
    var pos: Int = 0,
    var isSelected: Boolean = false
) {

    private fun getMonthStr(month: String?): String? {
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMM")
        val monthnum = month?.toInt()
        cal[Calendar.MONTH] = monthnum ?: 0
        val month_name = month_date.format(cal.time)
        return month_name
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return String.format("%02d-%02d-%04d", day, month, year)
    }

    // -1 is because the month is 0 indexed
    private fun getMonthInt(monthName: String): Int {
        return when (monthName.lowercase(Locale.ROOT)) {
            "jan", "gen" -> 1 - 1
            "feb", "feb" -> 2 - 1
            "mar", "mar" -> 3 - 1
            "apr", "apr" -> 4 - 1
            "may", "mag" -> 5 - 1
            "jun", "giu" -> 6 - 1
            "jul", "lug" -> 7 - 1
            "aug", "ago" -> 8 - 1
            "sep", "set" -> 9 - 1
            "oct", "ott" -> 10 - 1
            "nov", "nov" -> 11 - 1
            "dec", "dic" -> 12 - 1
            // if the month name is already an int 0-12 indexed then just return it as is. Otherwise throw an exception
            else -> monthName.toIntOrNull() ?: throw Exception("Invalid month name: $monthName")
        }
    }

    fun getSelectedDate(): String? {
        val calendar = Calendar.getInstance()
        println("MONTH: $month) --> ${getMonthInt(month!!)}")
        calendar.set(year?.toInt() ?: 0, getMonthInt(month!!), date?.toInt() ?: 0)
        println("calendar.time: ${calendar.time}")
        val dateFormat = Converters().fromDate(calendar.time)
        println("DATE: $dateFormat")
        return dateFormat
    }
}
