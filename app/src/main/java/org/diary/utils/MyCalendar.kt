package org.diary.utils

import java.text.SimpleDateFormat
import java.util.*

object MyCalendar {
    val MILLIS_IN_DAY = 1000 * 60 * 60 * 24
    val FIRST_DAY = 18627  // 01-01-2021
    val DATE_TYPE_DD = 1
    val DATE_TYPE_DDMM = 2
    val DATE_TYPE_DDMMYY = 3
    val DATE_TYPE_DDMMYYYY = 4

    fun getTodayDD_MM_YYYY(): String? {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())
    }

    /**
     * Get yesterday
     */
    fun getCalendarYesterday(calendar: Calendar): Calendar? {
        calendar.add(Calendar.DATE, -1)
        return calendar
    }

    fun getCalendarTomorrow(calendar: Calendar): Calendar? {
        calendar.add(Calendar.DATE, +1)
        return calendar
    }

    /**
     * Get date as string
     *
     * @return
     */
    fun calendarToDateDD_MM_YYYY(calendar: Calendar): String? {
        val df = SimpleDateFormat("dd-MM-yyyy")
        return df.format(calendar.time)
    }

    fun todayToDay(): Int{
        // TODO different years
        var calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val dayOfYear = calendar[Calendar.DAY_OF_YEAR]
        return dayOfYear
    }

    fun calendarToDay(calendar: Calendar): Int{
        var millis = calendar.timeInMillis
        return ((millis / MILLIS_IN_DAY).toInt() - FIRST_DAY)
    }

    fun dayToDate(myDay: Int, dateType: Int ): String{
        var calendar = Calendar.getInstance()
        calendar.setTimeInMillis((myDay + FIRST_DAY).toLong() * MILLIS_IN_DAY.toLong())

        if (dateType == DATE_TYPE_DD){
            return SimpleDateFormat("dd").format(calendar.time)
        }

        if (dateType == DATE_TYPE_DDMM){
            return SimpleDateFormat("dd-MM").format(calendar.time)
        }
        if (dateType == DATE_TYPE_DDMMYY){
            return SimpleDateFormat("dd-MM-yy").format(calendar.time)
        }
        return SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
    }

    fun dateToDay(dateStr: String): Int{
        var calendar = Calendar.getInstance()
        var dateParts = dateStr.split("-")
        if (dateParts.size == 3) {
            calendar.set(Calendar.DAY_OF_MONTH, MyConverter.toInt(dateParts[0]))
            calendar.set(Calendar.MONTH, MyConverter.toInt(dateParts[1]) - 1)
            calendar.set(Calendar.YEAR, MyConverter.toInt(dateParts[2]) + 2000)
            var day = calendarToDay(calendar)
            return day
        }
        else {
            MyLogger.e("MyCalendar - dateToDay error")
        }
        return 0
    }


}