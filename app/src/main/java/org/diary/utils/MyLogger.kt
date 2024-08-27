package org.diary.utils

import android.util.Log

class MyLogger {

    companion object aa{
        const val MY_TAG = "newApp"

        fun d(str: String?) {
            Log.d(MY_TAG, str)
        }

        fun e(str: String?) {
            Log.e(MY_TAG, str)
        }

        fun s(str: String?) {
            Log.e(MY_TAG, str)
        }
    }
}
