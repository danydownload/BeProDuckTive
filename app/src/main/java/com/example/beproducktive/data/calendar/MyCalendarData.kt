package com.example.beproducktive.data.calendar

import java.text.SimpleDateFormat
import java.util.Calendar

data class MyCalendarData(private var startday: Int) {
    private var currentmonth: Int = 0
    private var currentyear: Int = 0
    private var dayofweek: Int = 0
    private var stringDayofWeek: String? = null
    private var dateFormat = SimpleDateFormat("E")
    private var calendar: Calendar = Calendar.getInstance()

    init {
        calendar.add(Calendar.DATE, startday)
        setThis()
    }

    private fun setThis() {
        startday = calendar.get(Calendar.DAY_OF_MONTH)
        currentmonth = calendar.get(Calendar.MONTH)
        currentyear = calendar.get(Calendar.YEAR)
        dayofweek = calendar.get(Calendar.DAY_OF_WEEK)
        stringDayofWeek = dateFormat.format(calendar.time)
    }

    fun getNextWeekDay(nxt: Int) {
        calendar.add(Calendar.DATE, nxt)
        setThis()
    }

    fun getWeekDay(): String? {
        return stringDayofWeek
    }

    fun getYear(): Int {
        return currentyear
    }

    fun getMonth(): Int {
        return currentmonth
    }

    fun getDay(): Int {
        return startday
    }
}
