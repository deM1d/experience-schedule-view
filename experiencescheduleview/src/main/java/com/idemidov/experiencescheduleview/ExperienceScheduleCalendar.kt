package com.idemidov.experiencescheduleview

import java.util.*

class ExperienceScheduleCalendar {

    private val calendar = Calendar.getInstance()
    var currentYear = calendar.get(Calendar.YEAR)
    var currentMonth = calendar.get(Calendar.MONTH)

    init {
        calendar.firstDayOfWeek = Calendar.MONDAY
    }

    fun setDate(date: Int) {
        calendar.set(currentYear, currentMonth, date, 0, 0, 0)
    }

    fun setTime(timeInMillis: Long) {
        calendar.timeInMillis = timeInMillis
        currentYear = getYear()
        currentMonth = getMonth()
    }

    fun increaseCurrentMonth() {
        currentMonth++
        if (currentMonth == 12) {
            currentMonth = 0
            currentYear++
        }
        invalidateCurrentDate()
    }

    fun decreaseCurrentMonth() {
        currentMonth--
        if (currentMonth == -1) {
            currentMonth = 11
            currentYear--
        }
        invalidateCurrentDate()
    }

    fun getYear() = calendar.get(Calendar.YEAR)

    fun getMonth() = calendar.get(Calendar.MONTH)

    fun getDayOfWeek() = calendar.get(Calendar.DAY_OF_WEEK)

    fun getDayOfMonth() = calendar.get(Calendar.DAY_OF_MONTH)

    fun getWeekOfMonth() = calendar.get(Calendar.WEEK_OF_MONTH)

    fun getMaxWeekOfMonth() = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)

    fun getMaxDayOfMonth() = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    fun getTimeInMillis() = calendar.timeInMillis

    private fun invalidateCurrentDate() {
        calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
    }
}