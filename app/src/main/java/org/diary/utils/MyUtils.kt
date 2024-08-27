package org.diary.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.diary.R
import org.diary.common.MyApplication
import org.diary.ui.fragments.HomeFragment
import java.util.*


object MyUtils {

    fun sleep(interval: Long) {
        if (interval <= 0) {
            return
        }
        try {
            Thread.sleep(interval)
        } catch (e: InterruptedException) {
        }
    }

    fun after(delay: Long, process: () -> Unit) {
        Handler().postDelayed({
            process()
        }, delay)
    }

    fun waitGotoHome(fragment: Fragment) {
        hideKeyboard()
        after(500, {
            changeFragment(fragment, HomeFragment(), null)
        })
    }

//    fun waitGotoHome(fragment: Fragment) {
//        var count = 0
//        hideKeyboard()
//        var countDownTimer = object :
//            CountDownTimer(MyCommon.MAX_LONG, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                MyLogger.d("MyUtils - waitGotoHome")
//                if (count++ == 1){
//                    changeFragment(fragment, HomeFragment(), null)
//                    cancel()
//                }
//            }
//            override fun onFinish() {
//            }
//        }
//        countDownTimer.start()
//    }

//    fun waitGotoHome(fragment: Fragment) {
//        var countDownTimer = object :
//            CountDownTimer(MyCommon.MAX_LONG, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                MyLogger.d("MyUtils - waitGotoHome")
//                var visibility = checkKeyboard()
//                MyLogger.d("MyUtils - waitGotoHome visibility=" + visibility)
//                if (visibility){
//                    hideKeyboard()
//                }
//                else {
//                    cancel()
//                    changeFragment(fragment, HomeFragment(), null)
//                }
//            }
//            override fun onFinish() {
//            }
//        }
//        countDownTimer.start()
//    }
//

    fun changeFragment(fragmentOld: Fragment, fragmentNew: Fragment?, bundle: Bundle?) {
        val myApplication: MyApplication = MyApplication.instance!!
//        MyLogger.d("MyUtils - changeFragment - myApplication=" + myApplication + " old=" + fragmentOld.toString() + " new=" + fragmentNew.toString())
//        hideAnyKeyboard()
        var fragmentManager: FragmentManager? = null
        if (myApplication != null) {
            fragmentManager = fragmentOld.requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            if (bundle != null) {
                fragmentNew?.arguments = bundle
            }
            transaction.replace(R.id.fragment_container_view, fragmentNew!!)
            transaction.addToBackStack(null)
            transaction.commit()
            fragmentManager
        }
        MyUtils.sleep(500)
        hideActionBar()
//        if (MyApplication.instance?.fragment.toString().contains("HomeFragment")) {
//            hideAnyKeyboard()
//        }
//        val imm = MyApplication.instance?.activity
//            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        var activity: AppCompatActivity = MyApplication.instance?.activity!!
//        imm.hideSoftInputFromWindow(activity.getRootView().getWindowToken(), 0)
////        imm.hideSoftInputFromWindow(MyApplication.instance?.activity.getView().getWindowToken(), 0)
    }

//    fun sortAll(books: MutableList<Book>): MutableList<Book>{
//        MyLogger.d("MyUtils - sortAll size=" + books.size)
//        var booksSorted: MutableList<Book> = ArrayList()
//        for (bookParent: Book in books){
//            MyLogger.d("----MyUtils - sortAll " + bookParent.name + " category=" + bookParent.typeCategory + " expanded=" + bookParent.expanded)
//            if (bookParent.typeCategory){
//                MyLogger.d("MyUtils - sortAll - add parent")
//                booksSorted.add(bookParent)
//                if (bookParent.expanded) {
//                    for (bookChild: Book in books) {
//                        MyLogger.d("MyUtils - sortAll - check if child=" + bookChild.name + " isCategory=" + bookChild.typeCategory)
//                        if (bookChild.top.equals(bookParent.name)) {
//                            MyLogger.d("MyUtils - sortAll - add to list for " + bookParent.name)
//                            booksSorted.add((bookChild))
//                        }
//                    }
//                }
//            }
//        }
//        showAll(booksSorted)
//        return booksSorted
//    }


//    fun changeFragment(fragmentOld: Fragment, fragmentNew: Fragment?, bundle: Bundle?):
//{
//        val myApplication: MyApplication = MyApplication.instance!!
//        MyLogger.d("MyUtils - changeFragment - myApplication=" + myApplication)
//        var fragmentManager: FragmentManager? = null
//        if (myApplication != null) {
//            val fragmentManager = fragmentOld.requireActivity().supportFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//            if (bundle != null) {
//                fragmentNew?.arguments = bundle
//            }
//            transaction.replace(R.id.fragment_container_view, fragmentNew!!)
//            transaction.addToBackStack(null)
//            transaction.commit()
//            return fragmentManager
//
//            // below is also OK
////            var fr = myApplication.activity?.getFragmentManager()?.beginTransaction()
////            fr?.replace(R.id.fragment_container_view, fragmentNew)
////            fr?.commit()
//        }
//
//    }

//    fun passData(editTextInput: String) {
//        val bundle = Bundle()
//        bundle.putString("inputText", editTextInput)
//        val transaction = this.supportFragmentManager.beginTransaction()
//        val fragmentTwo = FragmentTwo()
//        fragmentTwo.arguments = bundle
//        transaction.replace(R.id.relativeLayout, fragmentTwo)
//        transaction.addToBackStack(null)
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        transaction.commit()
//    }

//    fun changeFragment(fragmentCurrent: Fragment?, fragmentNew: Fragment?) {
//        if (fragmentCurrent == null || fragmentCurrent.activity == null) {
//            MyLogger.e("MyUtils - changeFragment - fragmentCurrent == null || fragmentCurrent.getActivity()==null")
//            return
//        }
//        val trans = fragmentCurrent.activity!!.supportFragmentManager
//                .beginTransaction()
//        // IMPORTANT: We use the "root frame" defined in
//        //  "root_fragment.xml" as the reference to replace fragment
//        trans.replace(R.id.root_frame, fragmentNew!!)
//
//        // IMPORTANT: The following lines allow us to add the fragment
//        // to the stack and return to it later, by pressing back
////                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////                    trans.addToBackStack(null);
//        trans.commit()
//    }

    fun hideActionBar() {
        if (true) {
            return
        }
        try {
            if (Build.VERSION.SDK_INT < 16) {
                MyApplication.instance?.activity
                    ?.window?.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
            } else {
                val decorView = MyApplication.instance
                    ?.activity?.window?.decorView
                if (decorView == null) {
                    MyLogger.e("MyUtils - hideActionBar decorView=$decorView")
                    return
                }

                // my old
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

                // hide action bar excluding MainActivity
//                if (!MyApplication.instance?.activity
//                        ?.getLocalClassName()?.contains("MainActivity")!!) {
                hideCompletelyActionBar()
            }
//            }
        } catch (e: Exception) {
            MyLogger.e("MyUtils - hideActionBar error=$e")
        }
        try {
            MyApplication.instance?.activity?.getWindow()
                ?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } catch (e: Exception) {
            MyLogger.e("MyUtils - hideActionBar error to keep screen on=$e")
        }
    }

    /**
     * Hide action bar
     */
    fun hideCompletelyActionBar() {
        // hide action bar
        try {
            val actionBar: ActionBar? = MyApplication.instance?.activity?.supportActionBar
            actionBar?.hide()
        } catch (e: Exception) {
//            MyLogger.e("hideCompletelyActionBar - error=" + e);
        }
        try {
            MyApplication.instance?.activity
                ?.supportActionBar!!.hide()
        } catch (e: Exception) {
        }
    }

    /**
     * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
     */
    fun hideSoftKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Try to hide the keyboard and returns whether it worked
     * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     * http://kotlinextensions.com/
     */
    fun View.hideKeyboard(): Boolean {
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        } catch (ignored: RuntimeException) { }
        return false
    }

    fun hideKeyboard() {
        MyLogger.d("HIDE KEYBOARD #############")
        MyApplication.instance?.editText!!.hideKeyboard()
    }

//    /**
//     * detect if opened
//     * https://proandroiddev.com/how-to-detect-if-the-android-keyboard-is-open-269b255a90f5
//     */
//    fun hideKeyboard() {
//        MyLogger.d("HIDE KEYBOARD #############")
//        var view: EditText = MyApplication.instance?.editText!!
//        if (view != null && view.requestFocus()) {
//            MyLogger.d("HIDE")
//            val imm =
//                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//            imm?.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
//        }
//    }
//
    fun showKeyboard(view: View) {
        MyLogger.d("SHOW KEYBOARD +++++++++++++++++++++")
        if (view.requestFocus()) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun checkKeyboard(): Boolean {
        MyLogger.d("checkKeyboard")
        var activity: Activity = MyApplication.instance?.activity!!
        val visibleBounds = Rect()
        activity.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = activity.getRootView().height - visibleBounds.height()
        val marginOfError = Math.round(activity.convertDpToPx(50F))
        return heightDiff > marginOfError
    }

//    fun checkKeyboard(layoutView: View): Boolean {
//        MyLogger.d("checkKeyboard - view=" + layoutView.toString())
//        val visibleBounds = Rect()
//        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
//        val heightDiff = getRootView().height - visibleBounds.height()
//        val marginOfError = Math.round(this.convertDpToPx(50F))
//        return heightDiff > marginOfError
//
////        var visibility = false
////        if (layoutView == null) {
////            MyLogger.d("checkKeyboard - view=null")
////            return false
////        }
////        layoutView.viewTreeObserver.addOnGlobalLayoutListener {
////            val rec = Rect()
////            layoutView.getWindowVisibleDisplayFrame(rec)
////
////            val screenHeight = layoutView.rootView.height
////            val keypadHeight = screenHeight - rec.bottom
////
////            if (keypadHeight > screenHeight * 0.15) {
////                MyLogger.d("keyboardVisibility - visible keypad=" + keypadHeight + " screen=" + screenHeight + " needKeyboard=" + MyApplication.instance?.needKeyboard)
////                visibility = true
//////                if (!MyApplication.instance?.needKeyboard!!) {
//////                    MyUtils.hideAnyKeyboard()
//////                }
////            } else {
////                MyLogger.d("keyboardVisibility - invisible keypad=" + keypadHeight + " screen=" + screenHeight)
////                visibility = false
////            }
////        }
////        MyLogger.d("checkKeyboard - visibility=" + visibility)
////        return visibility
//    }
//

    fun Activity.getRootView(): View {
        return findViewById<View>(android.R.id.content)
    }

    fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
    }

    fun Activity.isKeyboardOpen(): Boolean {
        val visibleBounds = Rect()
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = getRootView().height - visibleBounds.height()
        val marginOfError = Math.round(this.convertDpToPx(50F))
        return heightDiff > marginOfError
    }

    fun Activity.isKeyboardClosed(): Boolean {
        return !this.isKeyboardOpen()
    }

    fun getAppVersion(): String? {
        return try {
            val pInfo: PackageInfo =
                MyApplication.instance?.getPackageManager()?.getPackageInfo(
                    MyApplication.instance?.getPackageName(), 0
                )!!
            pInfo.versionCode.toString()
        } catch (e: NameNotFoundException) {
            MyLogger.e("MyUtils - getAppVersion - error=$e")
            ""
        }
    }

    /**
     * Get resolution
     */
    fun getScreenResolution() {
        val metrics: DisplayMetrics
        val widthPixels: Int
        metrics = DisplayMetrics()
        val myApplication: MyApplication = MyApplication.instance!!
        myApplication.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics)
        var densityDpi = myApplication.getResources().getDisplayMetrics().densityDpi
        widthPixels = metrics.widthPixels
        myApplication.screenWidth = widthPixels
        var scaleFactor = metrics.density
        val widthDp: Float = widthPixels / scaleFactor
        val heightDp: Float = metrics.heightPixels / scaleFactor
        MyLogger.d("MyUtils - getScreenResolution - size without navigation bar - width=" + metrics.widthPixels + " height=" + metrics.heightPixels)
        MyLogger.d("MyUtils - getScreenResolution - density=" + myApplication.getResources().getDisplayMetrics().density.toString() + " densityDpi=" + metrics.densityDpi.toString() + " width in dp=" + widthDp.toString() + " height in dp=" + heightDp)
        val density: Int = densityDpi
        if (density == DisplayMetrics.DENSITY_HIGH) {
            MyLogger.d("Density is high")
        } else if (density == DisplayMetrics.DENSITY_XHIGH) {
            MyLogger.d("Density is xhigh")
        } else if (density == DisplayMetrics.DENSITY_LOW) {
            MyLogger.d("Density is low")
        } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
            MyLogger.d("Density is medium")
        } else if (density == DisplayMetrics.DENSITY_XXHIGH) {
            MyLogger.d("Density is xxhigh")
        } else if (density == DisplayMetrics.DENSITY_XXXHIGH) {
            MyLogger.d("Density is xxxhigh")
        } else if (density == DisplayMetrics.DENSITY_TV) {
            MyLogger.d("Density is Tv")
        } else {
            MyLogger.d("Density is unknown")
        }
    }


}