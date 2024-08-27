package org.diary.common

import org.diary.R
import java.nio.channels.FileLock

class MyCommon {

    companion object {
        val DEBUG_FAST_START = true
        val DEBUG_ALERT_LOG = false
        val DEBUG_SHOW_BOOKS = false
        val DEBUG_LANG_ENG = false
        val STEPS_IN_X = 30
        val SIZE_ADD_EMPTY = 9

        //        var REGEX_INTEGER: Regex = Regex(pattern = "^\\d+$")
        var REGEX_INTEGER: Regex = Regex(pattern = "^([+-]?\\d+)$")
        var REGEX_FLOAT: Regex = Regex("^([+-]?\\d*\\.?\\d*)$")

        val EXT_STORAGE = true
        val LANG_DEFAULT: String = "RUS"
        val DEBUG_INIT = true
        val MAX_LONG: Long = 9999999
        val MAX_FLOAT: Float = 9999999f
        val MAX_INT: Int = 32000
        val MAX_DIALOG_WAIT: Long = 30000

        val TYPE_TABLE: Int = 1
        val TYPE_GRAPH: Int = 2
        val TYPE_RECT: Int = 3

        val DB_NAME = "diary.db"
        val DB_DIR = "/org.diary/"
        val DB_PATH = MyCommon.DB_DIR + MyCommon.DB_NAME
        val TABLE_NOTE = "n"

        val REQUEST_ADD_TOP = "REQUEST_ADD_TOP"
        val REQUEST_ADD_CHILD = "REQUEST_ADD_CHILD"
        val REQUEST_NUMBER: String = "REQUEST_NUMBER"
        val PARENT_NAME: String = "PARENT_NAME"
        val TITLE_STRING: String = "TITLE_STRING"
        val LINES_NUMBER: String = "LINES_NUMBER"
        val RESULT_STRING: String = "RESULT_STRING"
        val RESULT_TYPE: String = "RESULT_TYPE"
        val RESULT_MIN: String = "RESULT_MIN"
        val RESULT_MAX: String = "RESULT_MAX"

        val VALUE_TYPE_INTEGER = 1
        val VALUE_TYPE_FLOAT = 2
        val VALUE_TYPE_TEXT = 3
        val VALUE_TYPE_MAX = 2

        val SHOW_STEPS = arrayOf<Int>(1, 3, 5, 7, 10, 30, 90, 120, 365)
        val GENRES = arrayOf<String>("average", "frequency", "increasing")
        val GENRE_AVERAGE = 0
        val GENRE_FREQUENCY = 1
        val GENRE_INCREASING = 2

        val DIALOG_PERIOD: Long = 30 * 1000
        val TIME_TICK: Long = 1000

        val SCREEN_THREAD_START_INTERVAL: Long = 3000
        val SCREEN_THREAD_SLEEP_INTERVAL: Long = 1000
        val MAX_IMAGE_WIDTH = 4096

        val PAGE_HOME = 0
        val PAGE_OTHER = 0

        val COLORS = intArrayOf(
            MyApplication.instance?.resources?.getColor(R.color.colorGray)!!,
            MyApplication.instance?.resources?.getColor(R.color.colorRed)!!,
            MyApplication.instance?.resources?.getColor(R.color.colorGreen)!!,
            MyApplication.instance?.resources?.getColor(R.color.newBlue)!!,
            MyApplication.instance?.resources?.getColor(R.color.newCian)!!,
            MyApplication.instance?.resources?.getColor(R.color.colorYellow)!!
        )
    }
}