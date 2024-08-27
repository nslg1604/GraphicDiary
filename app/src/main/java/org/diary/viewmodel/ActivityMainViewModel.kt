package org.diary.viewmodel

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.CopyDatabase
import org.diary.model.MyDatabase
import org.diary.ui.fragments.FillFragment
import org.diary.ui.fragments.HomeFragment
import org.diary.ui.fragments.ShowFragment
import org.diary.utils.MyAlert
import org.diary.utils.MyFileUtils
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import kotlin.coroutines.CoroutineContext

const val PAGE_HOME: Int = 1
const val PAGE_FILL: Int = 2
const val PAGE_SHOW: Int = 3

class ActivityMainViewModel constructor(
    activity: AppCompatActivity,
    coroutineContext: CoroutineContext
) :
    ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = coroutineContext
    var activity = activity
    var myDatabase: MyDatabase ?= null

    // Create a LiveData
    val livePage: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val liveProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val myApplication: MyApplication = MyApplication.instance!!

    init {
        MyLogger.d("ActivityMainViewModel - init")
        showProgress()
        launch {
            val myProgress = async(Dispatchers.IO) {
                myDatabase = MyDatabase(true)
                myApplication.myDatabase = myDatabase
                var dbExists = MyFileUtils.isFileExist(myApplication.myDatabase?.databasePath!!)
                MyLogger.d("ActivityMainViewModel - init dbExists=" + dbExists)
                if (!dbExists) {
                    myApplication.myDatabase?.createTableBooks()
                    myApplication.myDatabase?.createTestData()
                    myApplication.bookCurrent = myApplication.myDatabase?.findFirstChildBook()
                }

                if (!MyCommon.EXT_STORAGE) {
                    myApplication.myDatabaseCopy = MyDatabase(false)
                    var dbCopyExists =
                        MyFileUtils.isFileExist(myApplication.myDatabaseCopy?.databasePath!!)
                    MyLogger.d("ActivityMainViewModel - init dbCopyExists=" + dbCopyExists)
                    if (!dbCopyExists) {
                        myApplication.myDatabaseCopy?.createTableBooks()
                    }
                }
            }
            cancelProgress(myProgress.await())
            MyLogger.d("ActivityMainViewModel - init AFTER cancelProgress")
        }

    }

    fun showProgress() {
        MyLogger.d("ActivityMainViewModel - showProgress")
        liveProgress.value = true
//        MyUtils.sleep(5000)
//        MyLogger.d("ActivityMainViewModel - showProgress - end")
    }

    fun cancelProgress(async: Unit) {
        MyLogger.d("ActivityMainViewModel - cancelProgress")
        liveProgress.value = false
        myApplication.activityMainViewModel?.selectHome()
    }

    fun addTextSchema(): String {
        return myApplication.getText("page_schema")
    }

    fun addTextFill(): String {
        MyLogger.d("ActivityMainViewModel - addTextFill")
        return myApplication.getText("page_fill")
    }

    fun addTextShow(): String {
        return myApplication.getText("page_show")
    }

    fun selectHome() {
        MyLogger.d("ActivityMainViewModel - selectCreate")
        var homeFragment = HomeFragment()
        MyUtils.changeFragment(MyApplication.instance?.fragment!!, homeFragment, null)
        livePage.value = PAGE_HOME
    }

    fun selectFill() {
        MyLogger.d("ActivityMainViewModel - selectFill")
        var fillFragment = FillFragment()
        val bundle = Bundle()
        bundle.putString(MyCommon.REQUEST_NUMBER, MyCommon.REQUEST_ADD_TOP)
        bundle.putInt(MyCommon.LINES_NUMBER, 1)
        MyUtils.changeFragment(MyApplication.instance?.fragment!!, fillFragment, bundle)
        livePage.value = PAGE_FILL
    }

    fun selectShow() {
        MyLogger.d("ActivityMainViewModel - selectShow activity=" + myApplication.activity + " a=" + myApplication.activity?.toString())
        MyLogger.d("ActivityMainViewModel - selectShow fragment=" + MyApplication.instance?.fragment!!.toString())
        if (myApplication?.bookCurrent == null) {
            myApplication?.bookCurrent = myApplication?.myDatabase?.findFirstChildBook()
        }
        MyLogger.d("ActivityMainViewModel - selectShow bookCurrent=" + myApplication?.bookCurrent?.name)
        if (myApplication?.bookCurrent == null) {
            MyLogger.e("ActivityMainViewModel - selectShow - no book to show")
            MyAlert.showAlertWithIconUi(
                myApplication,
                "",
                myApplication.getText("no_child"),
                MyAlert.ICON_ERROR
            )
            return
        }

        var showFragment = ShowFragment()
        MyUtils.changeFragment(MyApplication.instance?.fragment!!, showFragment, null)
        livePage.value = PAGE_SHOW
    }

    fun getTitle(): String {
        return MyApplication.instance?.getText("diary_title")!!

    }

    fun exitNow() {

    }

    /**
     * https://www.javatpoint.com/kotlin-android-popup-menu
     */
    fun selectMenu(view: View) {
        MyLogger.d("ActivityMainViewModel - selectMenu")
        val popupMenu = PopupMenu(myApplication.activity, view)
        if (myApplication.lang.equals("RUS")) {
            popupMenu.menuInflater.inflate(R.menu.menu_main_rus, popupMenu.menu)
        }
        else {
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)
        }
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_exit -> {
                    MyLogger.d("Exit")
                    myApplication.finishNow = true
                    myApplication.activity.finish()
                }

//                R.id.action_copy -> {
//                    MyLogger.d("Copy")
//                    copyDb(
//                        myApplication.getText("copy_info"),
//                        myDatabase?.databasePath!!,
//                        Environment.getExternalStoragePublicDirectory(MyCommon.DB_PATH).toString()
//                    )
//                }
//
//                R.id.action_restore -> {
//                    MyLogger.d("Restore")
//                    // todo restore
////                    myApplication.myDatabase?.close()
////                    MyFileUtils.remove(MyDatabase.databasePath!!)
//                    copyDb(
//                        myApplication.getText("restore_info"),
//                        Environment.getExternalStoragePublicDirectory(MyCommon.DB_PATH).toString(),
//                        myDatabase?.databasePath!!)
////                    myApplication.myDatabase = MyDatabase()
//                    selectHome()
////                    val intent = Intent(
////                        MyApplication.instance,
////                        TitleActivity::class.java
////                    )
////                    MyApplication.instance?.startActivity(intent)
//
//                }

                R.id.action_help -> {
                    MyLogger.d("Help")
                    MyAlert.showAlertWithIconUi(
                        myApplication,
                        "",
                        "",
                        MyAlert.ICON_HELP
                    )
                }

                R.id.action_about -> {
                    MyLogger.d("About")
                    var text = myApplication.getText("about_info")
                    text = text.replace("%version%", MyUtils.getAppVersion()!!)
                    MyAlert.showAlertWithIconUi(
                        myApplication,
                        "",
                        text,
                        MyAlert.ICON_INFO
                    )
                }
            }
            true
        })
        popupMenu.show()
    }

    fun copyDb(text: String, srcPath: String, dstPath: String) {
        launch {
            val myProgress = async(Dispatchers.IO) {
                MyAlert.showAlertUiNoButton(
                    activity,
                    text,
                    MyAlert.ICON_SAVING
                )
                MyUtils.sleep(5000)
            }
            cancelAlertAndCopy(myProgress.await(), srcPath, dstPath)
        }
    }

    fun cancelAlertAndCopy(async: Unit, srcPath: String, dstPath: String) {
        MyLogger.d("ActivityMainViewModel - cancelAlert")
//        MyFileUtils.copyFile(
//            srcPath,
//            dstPath
//        )

        var copyDatabase = CopyDatabase()
        if (srcPath == myDatabase?.databasePath!!) {
            copyDatabase.myCopy()
        }
        else {
            copyDatabase.myRestore()
        }
        MyAlert.cancelAlert(0)
    }

    fun onBackPressed() {
        MyLogger.d("ActivityMainViewModel - onBackPressed")
        if (myApplication.bookToChangeOrder != null){
            if (myApplication.fragment!!.toString().contains("HomeFragment")){
                myApplication.bookViewModel?.hideUpDown(
                    MyApplication.instance?.bookToChangeOrder!!)
            }
        }

        // Close dialog
        if (MyAlert.dialog != null){
            MyLogger.d("ActivityMainViewModel - onBackPressed - found alert")
            MyAlert.cancelAlert(0)
            return
        }

        if (myApplication.fragment!!.toString().contains("HomeFragment")){
            return
        }
        MyUtils.changeFragment(myApplication.fragment!!, HomeFragment(), null)
    }

}
