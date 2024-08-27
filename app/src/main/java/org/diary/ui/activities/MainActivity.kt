package org.diary.ui.activities

//import org.diary.databinding.MainActivityBinding

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.diary.R
import org.diary.common.MyApplication
import org.diary.databinding.ActivityMainBinding
import org.diary.utils.MyCalendar
import org.diary.utils.MyConverter
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.ActivityMainViewModel
import org.diary.viewmodel.PAGE_FILL
import org.diary.viewmodel.PAGE_HOME
import org.diary.viewmodel.PAGE_SHOW
import java.util.*
import kotlin.coroutines.CoroutineContext


/**
 * keyboard:
 * https://www.raywenderlich.com/18393648-window-insets-and-keyboard-animations-tutorial-for-android-11
 */
class MainActivity : AppCompatActivity(), CoroutineScope {
    // for coroutine
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    private lateinit var activityMainViewModel: ActivityMainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myApplication: MyApplication = MyApplication.instance!!
        myApplication.activity = this
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyUtils.getScreenResolution()
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.GRAY
        job = Job() // create the Job for coroutines
        MyLogger.d("MainActivity - activity=" + myApplication.activity)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainViewModel = ActivityMainViewModel(this, coroutineContext)
        binding.lifecycleOwner = this
        binding.xmlMainViewModel = activityMainViewModel
        myApplication?.activityMainViewModel = activityMainViewModel

        // Create the observer which updates the UI.
        val pageObserver = Observer<Int> { newValue ->
            setPagesMenuColor(newValue)
        }
        activityMainViewModel.livePage.observe(this, pageObserver)

        val progressObserver = Observer<Boolean> { newValue ->
            if (newValue) {
                binding.mainProgress.visibility = View.VISIBLE
            } else {
                binding.mainProgress.visibility = View.INVISIBLE
            }
        }
        activityMainViewModel.liveProgress.observe(this, progressObserver)

        myTest()
    }

    fun myTest(){
        MyLogger.d("MainActivity - myTest")
        var day = -200;
        while(day < 500) {
            var error = ""
            var dateStr = MyCalendar.dayToDate(day, MyCalendar.DATE_TYPE_DDMMYY)
            var dayBack = MyCalendar.dateToDay(dateStr)
            if (day != dayBack){
                error = " ****"
            }
            MyLogger.d("MainActivity - myTest day=" + day + "/" + dayBack + " date=" + dateStr + error)
            day += 10
        }
    }

    fun setPagesMenuColor(pageCurrent: Int) {
        MyLogger.d("MainActivity - setPagesMenuColor page=" + pageCurrent)

        when (pageCurrent) {
            PAGE_HOME -> {
            }
            PAGE_FILL -> {
            }
            PAGE_SHOW -> {
            }
        }
    }

    fun hideBar(async: Unit) {
        MyUtils.hideActionBar()
    }

    override fun onBackPressed() {
        MyLogger.d("MainActivity - onBackPressed")
        activityMainViewModel.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.instance?.finishNow!!) {
            finish()
        }
    }
}