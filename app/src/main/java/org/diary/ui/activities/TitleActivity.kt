package org.diary.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.databinding.ActivityTitleBinding
import org.diary.utils.MyCalendar
import org.diary.utils.MyLogger
import org.diary.viewmodel.ActivityTitleViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext


class TitleActivity : AppCompatActivity(), CoroutineScope {
    // for coroutine
    override val coroutineContext: CoroutineContext get() =
        Dispatchers.Main + job
    private lateinit var job: Job

    private lateinit var activityTitleViewModel: ActivityTitleViewModel
    private lateinit var binding: ActivityTitleBinding
    var owner: LifecycleOwner = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myApplication: MyApplication = MyApplication.instance!!
        myApplication.activity = this
        job = Job() // create the Job for coroutines
        MyLogger.d("TitleActivity - onCreate - activity=" + myApplication.activity)
        test()

        var lang = MyCommon.LANG_DEFAULT
        val iso3Lang = Locale.getDefault().isO3Language.toUpperCase()
        MyLogger.d("TitleActivity - ISO3=" + iso3Lang)
        if (!iso3Lang.equals(MyCommon.LANG_DEFAULT) ||
            MyCommon.DEBUG_LANG_ENG
        ) {
            lang = "ENG"
        }
        MyApplication.instance?.initLanguage(lang);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_title)
        activityTitleViewModel = ActivityTitleViewModel(coroutineContext)
        binding.lifecycleOwner = this
        binding.xmlTitleViewModel = activityTitleViewModel
        binding.buttonContinue.visibility = View.INVISIBLE

        val buttonObserver = Observer<Boolean> { newValue ->
            if (newValue) {
                binding.buttonContinue.visibility = View.VISIBLE
            } else {
                binding.buttonContinue.visibility = View.INVISIBLE
            }
        }
        activityTitleViewModel.buttonVisibility.observe(owner, buttonObserver)
    }

    fun test() {
        var today = MyCalendar.calendarToDay(Calendar.getInstance())
        MyLogger.d("TitleActivity - test - " + MyCalendar.calendarToDateDD_MM_YYYY(Calendar.getInstance()) + "=" + today)
        MyLogger.d(
            "TitleActivity - test - today=" + today + "/" + MyCalendar.dayToDate(
                today,
                MyCalendar.DATE_TYPE_DDMMYYYY
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.instance?.finishNow!!) {
            MyApplication.instance?.finishNow = false
            finish()
        }
    }
}