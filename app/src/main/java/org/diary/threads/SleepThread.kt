package org.diary.threads

import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.utils.MyUtils.hideActionBar


class ScreenThread(start: Boolean = true) : Thread() {
    override fun run() {

        MyLogger.d("ScreenThread - started")
        MyUtils.sleep(MyCommon.SCREEN_THREAD_START_INTERVAL)
        while (true) {
//            MyLogger.d("ScreenThread - loop")
            try {
                MyApplication.instance?.activity?.runOnUiThread {
                    MyUtils.hideActionBar() 
                }
            } catch (e: Exception) {
                MyLogger.d("ScreenThread - error=$e")
            }
            MyUtils.sleep(MyCommon.SCREEN_THREAD_SLEEP_INTERVAL)
        }
    }
}
