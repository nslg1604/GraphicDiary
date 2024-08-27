package org.diary.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.utils.MyUtils.sleep


object MyAlert {
    var PROGRESS_TYPE_OTHER = 0
    var PROGRESS_TYPE_PAYMENT = 2
    var PROGRESS_TYPE_UPDATE = 1
    var ICON_NO = 0
    var ICON_WARNING = 1
    var ICON_ERROR = 2
    var ICON_GOOD = 3
    var ICON_CONFIRM = 4
    var ICON_SAVING = 5
    var ICON_DOWNLOAD = 6
    var ICON_UPDATE = 7
    var ICON_PRINT = 8
    var ICON_PROCESS = 9
    var ICON_INFO = 10
    var ICON_QR = 11
    var ICON_HELP = 12
    private var progressBar: ProgressBar? = null
    var progressMessage: String? = null
        private set
    private var textViewProgress: TextView? = null
    private var progressEnd = 0
    private var progressCount = 0
    var dialog: Dialog? = null
    private var alertDialog: AlertDialog? = null

    @Volatile
    var isAlertFragmentActive = false
        private set

    @Volatile
    var isAlertProgressActive = false
        private set
    private var newFragment: DialogFragment? = null
    private var alertButtonYesTapped = false
    private var alertButtonNoTapped = false
    var isAlertWithButtonActive = false
        private set
    private var countDownTimerProgress: CountDownTimer? = null
    private var countDownTimerCancel: CountDownTimer? = null
    private var countDownTimerFinish: CountDownTimer? = null
    private var myApplication: MyApplication? = MyApplication.instance

    /**
     * Set progress count - it is accepted
     * if countDownTimer is started
     *
     * @param progressCount1
     */
    fun setProgressCount(progressCount1: Int) {
        progressCount = progressCount1
    }

    fun setMyApplication(myApplication1: MyApplication?) {
        myApplication = myApplication1
    }

    /**
     * Show error alert dialog
     */
    fun showErrorAlert(context: Context?, message: String) {
        initAlert()
        if (context == null) {
            return
        }
        myApplication = context.applicationContext as MyApplication

//        MyLogger.v("MyAlert - showErrorAlert context=" + context + " myApplication=" + myApplication + " message=" + message);
        showAlertWithIcon(
            context, myApplication!!.getText("error"), message,
            R.drawable.ic_alert
        )
        MyLogger.e("Error: $message")
    }

    /**
     * Show popup messages
     */
    fun showAlertWithIcon(
        context: Context?,
        title: String,
        message: String,
        iconId: Int
    ) {
        var message = message
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertWithIcon title=$title msg=$message")
        }
        if (context == null) {
            return
        }
        try {
            dialog = Dialog(context) // Context, this, etc.
//            dialog = Dialog(MyApplication.instance!!) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("showAlertWithIcon message=$message - new dialog not created")
                return
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            dialog?.setContentView(R.layout.dialog_with_icon)
//            dialog!!.setTitle(title)
            val imageView = dialog!!.findViewById<View>(R.id.dialog_icon_top) as ImageView
            imageView.setImageResource(selectIcon(iconId))
//            MyLogger.d("MyAlert - showAlertWithIcon titleView=" + dialog!!.findViewById<TextView>(R.id.title))
////            val titleView = dialog!!.findViewById<View>(R.id.title) as TextView
//            val titleView = dialog!!.findViewById<TextView>(R.id.title)
//            MyLogger.d("MyAlert - showAlertWithIcon titleView=" + titleView)
//            if (titleView != null) {
//                titleView.gravity = Gravity.CENTER
//                titleView.textSize = context.resources.getDimension(R.dimen.t_022)
//            }
            MyLogger.d(
                "MyAlert - showAlertWithIcon textViewMessage=" + dialog!!.findViewById<TextView>(
                    R.id.dialog_message
                )
            )
            val textViewMessage = dialog!!.findViewById<TextView>(R.id.dialog_message)
            MyLogger.d("MyAlert - showAlertWithIcon textViewMessage=" + textViewMessage)
            textViewMessage.text = message
            showDialog(dialog)
            val buttonDialogOk = dialog!!.findViewById<View>(R.id.dialog_ok) as Button
            MyLogger.d("MyAlert - showAlertWithIcon buttonDialogOk=" + buttonDialogOk)
            buttonDialogOk.setOnClickListener {
                alertButtonYesTapped = true
                cancelAlert(0)
            }
            buttonDialogOk.text = myApplication!!.getText("yes")
            return
        } catch (e: Exception) {
            MyLogger.e("showAlertWithIcon message=$message - error: $e")
            cancelAlert(0)
            return
        }
    }

    /**
     * Show popup messages
     */
    fun showAlertWithIconUi(
        myApplication: MyApplication,
        title: String,
        message: String,
        iconType: Int
    ) {
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertWithIconUi title=$title msg=$message")
        }
        try {
            myApplication.activity.runOnUiThread {
                showAlertWithIcon(
                    myApplication,
                    "",
                    message,
                    iconType
                )
            }
        } catch (e: Exception) {
            MyLogger.d("showAlertUiNoButton- error to initPickerForPos alert. message=$message")
            cancelAlert(0)
        }
    }

    /**
     * Show alert dialog
     */
    fun showAlertWithIcon(
        myApplication: MyApplication,
        title: String, message: String, iconType: Int
    ) {
        var message = message
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertWithIcon title=$title msg=$message iconType=$iconType")
        }
        if (myApplication == null) {
            return
        }
        try {
            dialog = Dialog(myApplication.activity) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("showAlertWithIcon message=$message - new dialog not created")
                return
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)

            if (iconType == ICON_HELP){
                dialog?.setContentView(R.layout.dialog_help)
                val imageView = dialog!!.findViewById<View>(R.id.dialog_icon_top) as ImageView
                if (myApplication.lang.equals("RUS")) {
                    imageView.setImageResource(R.drawable.ic_help_rus)
                }
                else {
                    imageView.setImageResource(R.drawable.ic_help_eng)
                }
            }
            else {
                dialog?.setContentView(R.layout.dialog_with_icon)
                val imageView = dialog!!.findViewById<View>(R.id.dialog_icon_top) as ImageView
                imageView.setImageResource(selectIcon(iconType))
            }

            val textViewMessage = dialog!!.findViewById<TextView>(R.id.dialog_message)
            textViewMessage.text = message
            showDialog(dialog)
            val buttonDialogOk = dialog!!.findViewById<View>(R.id.dialog_ok) as Button
            buttonDialogOk.setOnClickListener {
                alertButtonYesTapped = true
                cancelAlert(0)
            }
            buttonDialogOk.text = myApplication.getText("yes")
            return
        } catch (e: Exception) {
            MyLogger.e("showAlertWithIcon message=$message - error: $e")
            cancelAlert(0)
            return
        }
    }

    /**
     * Show popup messages
     *
     * @param context
     * @param title
     * @param message
     */
    fun showAlertConfirm(
//        context: Context?,
//        title: String,
        message: String
    ) {
        var myApplication = MyApplication.instance
        var message = message
        MyLogger.d("MyAlert - showAlertConfirm")
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
//            MyLogger.e("showAlertConfirm title=$title msg=$message")
            MyLogger.d("MyAlert - showAlertConfirm msg=$message")
        }
        try {
            MyLogger.d("MyAlert - showAlertConfirm - message=$message")
            dialog = Dialog(myApplication?.activity!!) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("MyAlert - showAlertConfirm - NULL activity=" + myApplication!!.activity.localClassName + " message=" + message)
                return
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            dialog?.setContentView(R.layout.dialog_yes_no)
//            if (true) {
//                dialog!!.setTitle(title)
//                val textViewTitle = dialog!!.findViewById<View>(R.id.title) as TextView
//                if (textViewTitle != null) {
//                    textViewTitle.gravity = Gravity.CENTER
//                    textViewTitle.textSize = context.resources.getDimension(R.dimen.t_022)
//                    textViewTitle.text = title
//                }
//            }
            val textViewMessage =
                dialog!!.findViewById<View>(R.id.dialog_confirm_message) as TextView
            textViewMessage.text = message
            textViewMessage.transformationMethod = null

            // Ok button
            val buttonDialogOk = dialog!!.findViewById<View>(R.id.dialog_confirm_ok) as Button
            buttonDialogOk.text = myApplication!!.getText("yes")
            buttonDialogOk.transformationMethod = null
            buttonDialogOk.setOnClickListener {
                MyLogger.d("MyAlert - showAlertConfirm - selected YES")
                alertButtonYesTapped = true
                alertButtonNoTapped = false
                if (dialog != null) {
                    isAlertFragmentActive = false
                    cancelAlert(0)
                }
            }

            // Cancel button
            val buttonDialogCancel =
                dialog!!.findViewById<View>(R.id.dialog_confirm_cancel) as Button
            buttonDialogCancel.transformationMethod = null
            buttonDialogCancel.setOnClickListener {
                MyLogger.d("MyUtils - showAlertConfirm - CANCEL")
                alertButtonNoTapped = true
                if (dialog != null) {
                    isAlertFragmentActive = false
                    cancelAlert(0)
                }
            }
            buttonDialogCancel.text = myApplication!!.getText("cancel")
            showDialog(dialog)
        } catch (e: Exception) {
            MyLogger.e("showAlertWithIcon message=$message - error:$e")
            cancelAlert(0)
        }
    }

    /**
     * Show popup messages
     *
     * @param message
     */
    fun showAlertNoButton(activity: AppCompatActivity?, message: String, iconType: Int): Dialog? {
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertNoButton - activity=" + activity!!.localClassName + " message=" + message + " iconType=" + iconType)
        }
//        if (iconType == ICON_UPDATE) {
//            showProgressRound(
//                myApplication,
//                message,
//                4,  // progressEnd
//                PROGRESS_TYPE_UPDATE
//            ) // progressType
//            return dialog
//        }

//        if (showAnyProgress(message, iconType)) {
//            return dialog
//        }
        try {
            if (MyCommon.DEBUG_ALERT_LOG) {
                MyLogger.e("showAlertNoButton - normal dialog activity=" + activity!!.localClassName + " message=" + message + " iconType=" + iconType)
            }

            if (activity == null) {
                return null
            }
            dialog = Dialog(activity) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("showAlertNoButton - NULL activity=" + activity.localClassName + " message=" + message)
                return null
            }
//            MyLogger.e("showAlertNoButton - dialog created")
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Calculate "enter" in message
            var count = 0
            for (i in 0 until message.length) {
                if (message.substring(i, i + 1) == "\n") {
                    count += 1
                }
            }
            if (count > 2) {
                dialog?.setContentView(R.layout.dialog_no_button_high)
                MyLogger.d("MyAlert - showAlertNoButton - high layout selected")
            } else {
                dialog?.setContentView(R.layout.dialog_no_button)
            }
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val textViewMessage =
                dialog!!.findViewById<View>(R.id.dialog_no_button_message) as TextView
            textViewMessage.text = message
            val imageView = dialog!!.findViewById<View>(R.id.dialog_no_button_icon_top) as ImageView
            imageView.setImageResource(selectIcon(iconType))
            showDialog(dialog)
        } catch (e: Exception) {
            MyLogger.e("showAlertNoButton - error: $e")
            cancelAlert(0)
        }
        return dialog
    }

    /**
     * Select icon to use
     *
     * @param iconType
     * @return
     */
    private fun selectIcon(iconType: Int): Int {
        var iconType = iconType
        var icon: Int = R.drawable.ic_info_small
        if (iconType == ICON_ERROR) {
            icon = R.drawable.ic_alert
        } else if (iconType == ICON_WARNING) {
            icon = R.drawable.ic_warning
        } else if (iconType == ICON_GOOD) {
            icon = R.drawable.ic_good
        } else if (iconType == ICON_INFO) {
            icon = R.drawable.ic_info_round
            MyLogger.d("MyAlert - ICON_INFO icon=$icon")
        } else if (iconType == ICON_SAVING) {
//            icon = R.drawable.ic_saving_now
            icon = R.drawable.ic_copy2
        } else if (iconType == ICON_DOWNLOAD) {
//            icon = R.drawable.ic_downloding_now;
            iconType = ICON_PROCESS
        } else if (iconType == ICON_UPDATE) {
            iconType = ICON_PROCESS
            //            icon = R.drawable.ic_downloding_now;
        } else if (iconType == ICON_PRINT) {
            iconType = ICON_PROCESS
            //            icon = R.drawable.ic_printing_now;
        }
        MyLogger.d("MyAlert - iconType=$iconType icon=$icon")
        return icon
    }

    /**
     * Show progress dialog
     */
//    private fun showAnyProgress(message: String, iconType: Int): Boolean {
//        MyLogger.d("MyAlert - showAnyProgress - message=$message iconType=$iconType")
//        if (iconType == ICON_PROCESS || iconType == ICON_DOWNLOAD || iconType == ICON_PRINT) {
//            showProgressRound(
//                myApplication,
//                message,
//                4,  // progressEnd
//                iconType
//            ) // progressType
//        showAlertNoButton(MyApplication.instance?.activity,
//        message,
//        MyAlert.ICON_SAVING)
//            return true
////        }
//        return false
//    }

    fun isAlertButtonNoTapped(): Boolean {
        return alertButtonNoTapped
    }

    fun isAlertButtonYesTapped(): Boolean {
        return alertButtonYesTapped
    }

    fun setAlertButtonYesTapped(alertButtonYesTapped1: Boolean) {
        alertButtonYesTapped = alertButtonYesTapped1
    }

    /**
     * possible ways:
     * https://github.com/MRezaNasirloo/CircularProgressBar
     *
     *
     *
     *
     * progressEnd1 - duration in seconds
     */
    fun showProgressRoundUi(
        myApplication: MyApplication?,
        message: String,
        progressEnd: Int,
        progressType: Int
    ) {
        try {
            myApplication!!.activity.runOnUiThread {
                showProgressRound(
                    myApplication,
                    message,
                    progressEnd,
                    progressType
                )
            }
        } catch (e: Exception) {
            MyLogger.d("showAlertUiNoButton- error to initPickerForPos alert. message=$message")
            cancelAlert(0)
        }
    }

    /**
     * Test round progress bar
     */
    fun showRoundAlertUiTest() {
        showProgressRoundUi(
            myApplication, "Please wait. This is my progress bar for test", 100,
            PROGRESS_TYPE_PAYMENT
        )
        //        MyUtils.sleep(30000);
    }

    /**
     * Show progress window
     * https://stackoverflow.com/questions/21333866/how-to-create-a-circular-progressbar-in-android-which-rotates-on-it
     * http://www.skholingua.com/android-basic/user-interface/form-widgets/progressbar
     */
    fun showProgressRound(
        myApplication: MyApplication?,
        message: String,
        progressEnd1: Int,
        progressType: Int
    ): Dialog? {
        initAlert()
        MyLogger.d("MyAlert - showProgressRound end1=$progressEnd1 progressType=$progressType")
        progressEnd = progressEnd1
        progressCount = 0
        try {
            dialog = Dialog(myApplication!!.activity) // Context, this, etc.

            // set color
            var layoutId: Int = R.layout.dialog_round_cian
            if (progressType == PROGRESS_TYPE_UPDATE) {
                MyLogger.d("MyAlert - showProgressRound PROGRESS_TYPE_UPDATE")
                layoutId = R.layout.dialog_round_green
            }
            dialog!!.setContentView(layoutId)
            MyLogger.d("MyAlert - showProgressRound progressType=$progressType")
            dialog!!.window!!.setBackgroundDrawableResource(R.color.newWhite)
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            textViewProgress = dialog!!.findViewById<View>(R.id.progress_round_message) as TextView
            progressBar = dialog!!.findViewById<View>(R.id.progress_round_bar) as ProgressBar
            progressBar!!.secondaryProgress = 100
            progressMessage = message
            textViewProgress!!.text = message
            isAlertProgressActive = true
            showDialog(dialog)
            if (progressEnd1 > 0) {
                startCountDownTimerProgress() // showProgressRound
            }
        } catch (e: Exception) {
            MyLogger.e("MyUtils - showProgressRound message=$message error=$e")
            cancelAlert(0)
        }
        return dialog
    }

    /**
     * Show any progress window
     *
     * @param message progressEnd1 - duration in seconds
     * @return
     */
    fun showProgressDialog(
        myApplication: MyApplication,
        message: String,
        progressEnd1: Int,
        progressType: Int
    ) {
        showProgressRound(
            myApplication,
            message,
            progressEnd1,
            progressType
        )
        return
        initAlert()
        progressEnd = progressEnd1
        progressCount = 0
        try {
            val context = myApplication.applicationContext
            MyLogger.d("MyUtils - showProgressDialog activity=" + myApplication.activity.localClassName)
            dialog = Dialog(myApplication.activity) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("showProgressDialog - NULL")
                return
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.dialog_with_progress)
            textViewProgress =
                dialog!!.findViewById<View>(R.id.dialog_no_button_message) as TextView
            progressMessage = message
            textViewProgress!!.text = message
            progressBar = dialog!!.findViewById<View>(R.id.dialog_progress_bar) as ProgressBar
            if (progressType == PROGRESS_TYPE_UPDATE) {
                progressBar!!.progressDrawable =
                    context.resources.getDrawable(R.drawable.my_progress_blue)
            } else if (progressType == PROGRESS_TYPE_PAYMENT) {
                progressBar!!.progressDrawable =
                    context.resources.getDrawable(R.drawable.my_progress_bar_green)
            } else {
                progressBar!!.progressDrawable =
                    context.resources.getDrawable(R.drawable.my_progress_purple)
            }
            //            progressBar.setProgress(progressStart);
            isAlertProgressActive = true
            showDialog(dialog)
            if (progressEnd1 > 0) {
                startCountDownTimerProgress() // showProgressDialog
            }
        } catch (e: Exception) {
            MyLogger.e("MyUtils - showProgressDialog message=$message error=$e")
            cancelAlert(0)
        }
        return
    }

    /**
     * Set progress in progress window.
     * It is accepted
     * if no countDownTimer
     *
     * @param progress - percent
     */
    fun setProgress(progress: Int) {
//        MyLogger.d("UpdateDatabaseThread - setProgress=" + progress);
        if (progressBar != null) {
            try {
                progressBar!!.progress = progress
            } catch (e: Exception) {
                MyLogger.e("MyUtils - setProgress error:$e")
                cancelAlert(0)
            }
        }
    }

    /**
     * Set progress text
     */
    fun setProgressMessage(progressMessage1: String) {
        if (textViewProgress != null) {
            try {
                progressMessage = progressMessage1
                textViewProgress!!.text = progressMessage1
            } catch (e: Exception) {
                MyLogger.e("MyUtils - setProgressMessage progressMessage=$progressMessage1 error:$e")
                cancelAlert(0)
            }
        }
    }

    /**
     * Progress bar in UI
     */
    fun showProgressDialogUi(
        myApplication: MyApplication,
        message: String,
        progressEnd: Int,
        progressType: Int
    ) {
        try {
            myApplication.activity.runOnUiThread {
                showProgressDialog(
                    myApplication,
                    message,
                    progressEnd,
                    progressType
                )
            }
        } catch (e: Exception) {
            MyLogger.d("showAlertUiNoButton- error to initPickerForPos alert. message=$message")
            cancelAlert(0)
        }
    }

    /**
     * Countdown timer to show payment progress bar
     */
    private fun startCountDownTimerProgress() {
        stopCountDownTimerProgress()
        //        MyLogger.d("MyAlert - startCountDownTimerProgress");
        countDownTimerProgress = object : CountDownTimer(
            MyCommon.MAX_LONG, 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
//                if (progressBar != null) {
                if (dialog != null) {
                    if (progressEnd <= 0) {
                        return
                    }
                    val progressToShow = progressCount * 100 / progressEnd
                    try {
//                        MyLogger.d("MyAlert - startCountDownTimerProgress tick progressToShow=" + progressToShow);
                        setProgress(progressToShow)
                        progressCount += 1
                    } catch (e: Exception) {
                        MyLogger.e("UpdateDatabaseThread - setProgress error:$e")
                        cancelAlert(0)
                    }
                }
            }

            override fun onFinish() {}
        }
        countDownTimerProgress?.start()
    }

    /**
     * Stop payment countdown timer
     */
    fun stopCountDownTimerProgress() {
        if (countDownTimerProgress != null) {
            countDownTimerProgress!!.cancel()
            countDownTimerProgress = null
        }
    }

    /**
     * Start cancel dialog countDown timer
     */
    fun startCancelDialogTimer(interval: Long): CountDownTimer? {
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.d("MyAlert - startCancelDialogTimer interval=$interval")
        }
        countDownTimerCancel = object : CountDownTimer(interval, interval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (MyCommon.DEBUG_ALERT_LOG) {
                    MyLogger.d("MyAlert - startCancelDialogTimer onFinish")
                }
                cancelAlert(0)
            }
        }
        countDownTimerCancel?.start()
        return countDownTimerCancel
    }

    /**
     * Finish countDownTimer
     *
     * @return
     */
    fun startFinishDialogCountDownTimer(myApplication: MyApplication?): CountDownTimer? {
        val intervalTick: Long = 1000
        countDownTimerFinish = object : CountDownTimer(
            MyCommon.MAX_DIALOG_WAIT,
            intervalTick
        ) {
            override fun onTick(millisUntilFinished: Long) {
                if (alertButtonYesTapped) {
                    myFinish()
                }
            }

            override fun onFinish() {
                cancelAlert(0)
            }
        }
        countDownTimerFinish?.start()
        return countDownTimerFinish
    }

    /**
     * Finish
     */
    private fun myFinish() {
        cancelAlert(0)
        myApplication!!.activity.finish()
        countDownTimerFinish!!.cancel()
    }

    /**
     * initPickerForPos alert
     *
     * @param message
     */
    fun showAlertUiNoButton(activity: AppCompatActivity, message: String, iconType: Int) {
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertUiNoButton - activity=" + activity.localClassName + " message=" + message + " iconType=" + iconType)
        }
        try {
            activity.runOnUiThread {
                dialog = showAlertNoButton(
                    activity,
                    message,
                    iconType
                )
            }
        } catch (e: Exception) {
            MyLogger.d("showAlertUiNoButton- error to initPickerForPos alert. message=$message")
            cancelAlert(0)
        }
    }

    /**
     * initPickerForPos alert
     *
     * @param message
     */
    fun showAlertUi(
//        context: Context,
        activity: AppCompatActivity,
        message: String,
        iconType: Int
    ) {

        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertUi - activity=" + activity.localClassName + " message=" + message)
        }
        try {
            activity.runOnUiThread {
                showAlertWithIcon(
//                    context,
                    activity,
                    "",
                    message,
                    iconType
                )
            }
        } catch (e: Exception) {
            MyLogger.d("showAlertUiNoButton- error to initPickerForPos alert. message=$message")
            cancelAlert(0)
        }
    }

    /**
     * Cancel dialog alert window
     */
    fun cancelAlert(delay: Long) {
        sleep(delay)
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("-------------------------------- MyAlert - cancelAlert delay=$delay")
        }
        progressBar = null
        textViewProgress = null
        isAlertProgressActive = false
        isAlertFragmentActive = false
        isAlertWithButtonActive = false
        try {
            if (dialog != null) {
                dialog!!.cancel()
                dialog = null
            }
            if (alertDialog != null) {
                alertDialog!!.cancel()
                alertDialog = null
            }
        } catch (e: Exception) {
            MyLogger.e("MyUtils - cancelAlert error:$e")
        }
    }

    /**
     * Cancel dialog alert window
     */
    fun initAlert() {
        progressBar = null
        textViewProgress = null
        alertButtonYesTapped = false
        alertButtonNoTapped = false
        isAlertProgressActive = false
        isAlertFragmentActive = false
        isAlertWithButtonActive = false
        cancelAlert(0)
    }

    /**
     * Cancel dialog in fragment
     */
    fun cancelDialogFragment(sleepMillis: Long) {
        cancelAlertAfterSleep(sleepMillis)
    }

    /**
     * Cancel dialog in fragment
     */
    fun cancelAlertAfterSleep(sleepMillis: Long) {
        try {
            val countDownTimer: CountDownTimer = object : CountDownTimer(sleepMillis, 1500) {
                override fun onTick(millisUntilFinished: Long) {
//                            MyLogger.d("MyAlert - cancelAlertAfterSleep TICK");
                }

                override fun onFinish() {
//                            MyLogger.d("MyAlert - cancelAlertAfterSleep - FINISH");
                    cancelAlert(0)
                }
            }
            countDownTimer.start()
        } catch (e: Exception) {
            cancelAlert(0)
        }
    }

    /**
     * Show alert dialog with the message
     *
     * @param message
     */
    fun showAlertUiNoButton(
        myApplication: MyApplication,
        message: String, iconType: Int
    ) {
        initAlert()
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.e("showAlertUiNoButton - activity=" + myApplication.activity.localClassName + " message=" + message)
        }
        myApplication.activity.runOnUiThread {
            showAlertNoButton(
                myApplication.activity,
                message,
                iconType
            )
        }
    }

    /**
     * Check dialogs activity
     *
     * @return
     */
    fun isDialog(): Boolean {
//        MyLogger.v("---------------");
//        MyLogger.v("@@ dialog=" + dialog);
//        MyLogger.v("@@ alertDialog=" + alertDialog);
//        MyLogger.v("@@ alertFragmentActive=" + alertFragmentActive);
//        MyLogger.v("@@ alertProgressActive=" + alertProgressActive);
        return if (dialog != null || alertDialog != null ||
            isAlertFragmentActive || isAlertProgressActive
        ) {
            true
        } else false
    }

    /**
     * Set dialog parameters
     */
    private fun setDialogParams(dialog: Dialog?) {
        dialog!!.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
    }

    /**
     * Show dialog
     */
    private fun showDialog(dialog: Dialog?) {
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.d("MyAlert - showDialog")
        }
        setDialogParams(dialog)
        dialog!!.show()
//        MyUtils.hideActionBar()
        MyLogger.d("MyAlert - showDialog dialog=$dialog")
    }

    fun showBookActions(book: Book){
        initAlert()
        MyLogger.d("MyAlert - showBookActions book=" + book.name)
        var myApplication = MyApplication.instance
        try {
            dialog = Dialog(myApplication!!.activity) // Context, this, etc.
            if (dialog == null) {
                MyLogger.e("showSelect - NULL activity=" + myApplication!!.activity.localClassName)
                return
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.dialog_select)

            // title
            val textViewTitle = dialog!!.findViewById<TextView>(R.id.dialog_select_title)
            textViewTitle.text = book.name

            // Edit
            val buttonEdit =
                dialog!!.findViewById<View>(R.id.dialog_select_edit) as Button
            buttonEdit.transformationMethod = null
            buttonEdit.text = myApplication!!.getText("edit_name")
            buttonEdit.setOnClickListener {
                MyApplication.instance?.bookViewModel?.changeBook(book)
                cancelAlert(0)
            }

            // Edit
            val buttonChange =
                dialog!!.findViewById<View>(R.id.dialog_select_order) as Button
            buttonChange.transformationMethod = null
            buttonChange.text = myApplication!!.getText("edit_order")
            buttonChange.setOnClickListener {
                MyApplication.instance?.bookToChangeOrder = book
                myApplication.bookViewModel?.showUpDown(book)
                cancelAlert(0)
            }

            // remove
            val buttonRemove =
                dialog!!.findViewById<View>(R.id.dialog_select_remove) as Button
            buttonRemove.transformationMethod = null
            buttonRemove.text = myApplication!!.getText("remove")
            buttonRemove.setOnClickListener {
                myApplication.bookViewModel?.removeItem(book)
            }

            // Cancel button
            val buttonDialogCancel =
                dialog!!.findViewById<View>(R.id.dialog_select_back) as ImageButton
            buttonDialogCancel.setOnClickListener {
                cancelAlert(0)
            }
            showDialog(dialog)
        } catch (e: Exception) {
            MyLogger.e("showSelect unknown - error:$e")
            cancelAlert(0)
        }
    }

}
