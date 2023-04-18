package com.example.beproducktive.data.calendar

import com.example.beproducktive.utils.Converters
import java.text.SimpleDateFormat
import java.util.*

class MyCalendar {
    // storing day - like Sun, Wed etc..,
    // date like 1, 2 etc..
    //Month 1..12
    // year .. 2019.. beyond

    private var day: String? = null
    private var date: String? = null
    private var month: String? = null
    private var year: String? = null
    private var pos = 0
    var isSelected = false


    constructor() {}

    constructor(day: String?, date: String?, month: String?, year: String?, i: Int) {
        this.day = day
        this.date = date
        this.month = getMonthStr(month)
        this.year = year
        this.pos = i
    }

    private fun getMonthStr(month: String?): String? {
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMM")
        val monthnum = month?.toInt()
        cal[Calendar.MONTH] = monthnum ?: 0
        val month_name = month_date.format(cal.time)
        return month_name
    }


    fun getPos(): Int {
        return pos
    }

    fun getDay(): String? {
        return day
    }

    fun setDay(day: String?) {
        this.day = day
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun setMonth(month: String?) {
        this.month = month
    }

    fun getMonth(): String? {
        return month
    }

    fun getYear(): String? {
        return year
    }

    fun setYear(year: String?) {
        this.year = year
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
            else -> throw IllegalArgumentException("Invalid month name: $monthName")
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
