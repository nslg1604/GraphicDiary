package org.diary.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.ui.activities.MainActivity
import org.diary.utils.MyLogger
import org.diary.utils.MyPermissions
import org.diary.utils.MyUtils
import kotlin.coroutines.CoroutineContext


class ActivityTitleViewModel constructor(
    override val coroutineContext: CoroutineContext
) :
    ViewModel(), CoroutineScope {
    var myPermissions = MyPermissions()
    var delay: Long = 3000
//    var permissionAllowed = false

    // Create a LiveData
    val buttonVisibility: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        checkPermission()
    }

    fun gotoMainActivity(async: Unit) {
        MyLogger.d("ActivityMainViewModel - gotoMainActivity")

        val intent = Intent(
            MyApplication.instance,
            MainActivity::class.java
        )
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.instance?.startActivity(intent)
    }

    fun addTitle(): String {
        return MyApplication.instance?.getText("diary_title")!!
    }

    fun addVersion(): String {
        return MyApplication.instance?.getText("info_version")!! +
                " " + MyUtils.getAppVersion()
    }

    fun setTextButton(): String {
        return MyApplication.instance?.getText("continue")!!
    }

    fun clickContinue() {
        checkPermission()
    }

    fun checkPermission() {
        if (myPermissions.checkReadWritePermissions()) {
            buttonVisibility.value = false
            MyLogger.d("ActivityMainViewModel - permissions OK")
            launch {
                val myProgress = async(Dispatchers.IO) {
                    if (!MyCommon.DEBUG_FAST_START) {
                        MyUtils.sleep(delay)
                    }
                }
                gotoMainActivity(myProgress.await())
            }
        } else {
            MyLogger.d("ActivityMainViewModel - permissions NOT ALLOWED YET")
            buttonVisibility.value = true
            delay = 10
        }
    }
}