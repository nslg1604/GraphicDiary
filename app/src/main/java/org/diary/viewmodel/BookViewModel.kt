package org.diary.viewmodel

import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.BindingAdapter
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.ui.fragments.HomeFragment
import org.diary.model.Book
import org.diary.ui.fragments.ChildFragment
import org.diary.ui.fragments.ValueFragment
import org.diary.utils.MyAlert
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils

class BookViewModel constructor(homeFragment: HomeFragment) {
    private val TAB = "\u0009"
    private var homeFragment: HomeFragment = homeFragment
    private var book: Book? = null
    private var name = "MyName"
    private var myApplication: MyApplication = MyApplication.instance!!
    var countDownTimerConfirm: CountDownTimer? = null


    fun bind(book: Book) {
        name = book.name!!
        this.book = book
        MyApplication.instance?.bookCurrent = book
        MyApplication.instance?.bookViewModel = this
    }

    fun getName(): String {
        if (book?.level == Book.LEVEL_PARENT) {
            return name
        }
//        else if (book?.level == Book.LEVEL_MID) {
//            return TAB + TAB + name
//        }
        else {
//            return TAB + TAB + TAB + TAB + name
            return TAB + TAB + name
        }
    }


//    fun getImage(): Drawable {
//        if (book!!.expanded) {
//            return myApplication.resources.getDrawable(R.drawable.ic_expand_yes, null)
//        } else return myApplication.resources.getDrawable(R.drawable.ic_expand_no, null)
//    }

    object BindingUtils {
        private const val ON_LONG_CLICK = "android:onLongClick"

        @JvmStatic
        @BindingAdapter(ON_LONG_CLICK)
        fun setOnLongClickListener(
            view: View,
            func : () -> Unit
        ) {
            view.setOnLongClickListener {
                func()
                return@setOnLongClickListener true
            }
        }
    }

    fun selectName() {
        if (book?.name?.isEmpty()!!){
            return
        }
        MyAlert.showBookActions(book!!)
    }

    fun checkChanged(){
        MyLogger.d("BookViewModel - check changed")
        if (book?.icon?.isEmpty()!!) {
            myApplication.checkId = myApplication.checkId.plus(1)
            if (myApplication.checkId >= MyCommon.COLORS.size){
                myApplication.checkId = 0
            }
            book?.icon = myApplication.checkId.toString()
        }
        else {
            book?.icon = ""
        }
        myApplication.myDatabase?.addBook(book!!)
        myApplication.homeAdapter?.update()
    }

    fun getChecked(): Boolean{
        MyLogger.d("BookViewModel - getChecked name=" +  book?.name + " icon=" + book?.icon)
        if (book?.icon?.isEmpty()!!){
            return false
        }
        return true
    }

    fun checkVisibility(): Int{
        MyLogger.d("BookViewModel - checkVisibility - name=" + book?.name + " level=" + book?.level)
        if (myApplication.bookToChangeOrder != null &&
            book?.id.equals(myApplication.bookToChangeOrder?.id)) {
            MyLogger.d("BookViewModel - checkVisibility - name=" + book?.name + " level=" + book?.level + " INVISIBLE")
            return View.INVISIBLE
        }

        if (book?.level == Book.LEVEL_CHILD) {
            MyLogger.d("BookViewModel - checkVisibility - name=" + book?.name + " level=" + book?.level + " VISIBLE")
            return View.VISIBLE
        }
        else {
            MyLogger.d("BookViewModel - checkVisibility - name=" + book?.name + " type=" + book?.type + " INVISIBLE")
            return View.INVISIBLE
        }
    }

    fun arrowsVisibility(): Int{
        MyLogger.d("BookViewModel - getUpVisibility - name=" + book?.name + " level=" + book?.level)
        if (myApplication.bookToChangeOrder?.id.equals(book?.id)) {
            return View.VISIBLE
        }
        else {
            return View.INVISIBLE
        }
    }

//
//    fun downVisibility(): Int{
//        MyLogger.d("BookViewModel - getDownVisibility - name=" + book?.name + " level=" + book?.level)
//        if (myApplication.orderMode) {
//            return View.VISIBLE
//        }
//        else {
//            return View.INVISIBLE
//        }
//    }

    fun clickUp() {
        MyLogger.d("BookViewModel - clickUp book=" + book?.name + " level=" + book?.level)
    }

    fun clickDown() {
        MyLogger.d("BookViewModel - clickDown book=" + book?.name + " level=" + book?.level)
    }

    fun showUpDown(book: Book){
        this.book = book
        MyLogger.d("BookViewModel - showUpDown book=" + book?.name + " level=" + book?.level)
        myApplication.homeAdapter?.updateAll()
    }

    fun hideUpDown(book: Book){
        this.book = book
        myApplication.bookToChangeOrder = null
        MyLogger.d("BookViewModel - hideUpDown book=" + book?.name + " level=" + book?.level)
        myApplication.homeAdapter?.updateAll()
    }

    fun getButtonTint(): Int{
        MyLogger.d("BookViewModel - getButtonTint book?.icon=" + book?.icon)
        if (book?.icon?.isEmpty()!!){
            return myApplication.resources.getColor(R.color.colorGray)
        }
        var colorNumber: Int = book?.icon?.toInt()!!
        if (colorNumber >= MyCommon.COLORS.size){
            colorNumber = 0
        }
        return MyCommon.COLORS[colorNumber]
    }

    fun getLeftIcon(): Drawable {
        if (book!!.level == Book.LEVEL_CHILD) {
            return myApplication.resources.getDrawable(R.drawable.ic_change_black)
        } else if (book!!.level == Book.LEVEL_PARENT){
            return myApplication.resources.getDrawable(R.drawable.ic_add)
        }
        return myApplication.resources.getDrawable(R.drawable.ic_empty)
    }

    fun getRightIcon(): Drawable {
        if (book!!.level == Book.LEVEL_CHILD) {
            return myApplication.resources.getDrawable(R.drawable.ic_graph_right)
        }
        return myApplication.resources.getDrawable(R.drawable.ic_empty)
    }

    fun changeBook(book: Book) {
        MyLogger.d("BookViewModel - changeBook - book=" + book.name)
        this.book = book
        if (book.level == Book.LEVEL_PARENT) {
            myApplication?.bookCurrent = book
            myApplication?.bookChild = null
            MyUtils.changeFragment(homeFragment, ValueFragment(), null)
        }
        if (book.level == Book.LEVEL_CHILD) {
            myApplication?.bookCurrent = book
            myApplication?.bookChild = book
            myApplication?.bookParent =
                myApplication.myDatabase?.findBookById(book.id)
            editChild(book)  // changeBook
        }
    }

    fun editChild(book: Book) {
        this.book = book
        MyLogger.d("BookViewModel - editChild=" + book?.name + " level=" + book?.level)
        if (book!!.level == Book.LEVEL_PARENT) {
            MyLogger.d("BookViewModel - editChild - ADD to=" + book!!.name)
            myApplication?.bookParent = book
            myApplication?.bookChild = null
            MyUtils.changeFragment(homeFragment, ChildFragment(), null)
        }
        if (book!!.level == Book.LEVEL_CHILD) {
            MyLogger.d("BookViewModel - editChild - CHANGE to=" + book!!.name)
            myApplication?.bookParent = myApplication.myDatabase?.findBookById(book?.id!!)
            myApplication?.bookChild = book
            MyUtils.changeFragment(homeFragment, ChildFragment(), null)
        }
    }

    fun clickLeftLayout() {
        MyLogger.d("BookViewModel - clickLeftLayout book=" + book?.name + " level=" + book?.level)
        if (book?.name?.isEmpty()!!){
            return
        }
        myApplication?.bookCurrent = book
        if (book?.level == Book.LEVEL_PARENT) {
            MyLogger.d("BookViewModel - clickLeftLayout ADD child")
//            myApplication?.bookCurrent = book
//            MyUtils.changeFragment(homeFragment, ValueFragment(), null)
            editChild(book!!)  // create new child
        } else {
            MyApplication.instance?.bookParent =
                myApplication.myDatabase?.findBookById(book?.id!!)
            MyApplication.instance?.bookCurrent = book
            MyApplication.instance?.bookChild = book
            myApplication.activityMainViewModel?.selectFill()
        }
    }

    fun clickRightLayout() {
        if (book?.name?.isEmpty()!!){
            return
        }
        if (book!!.level == Book.LEVEL_CHILD) {
            MyApplication.instance?.bookChild = book
            MyApplication.instance?.bookCurrent = book
            myApplication.activityMainViewModel?.selectShow()
        }
    }

    fun removeItem(book: Book) {
        this.book = book
        MyLogger.d("BookViewModel - removeItem - book=" + book?.name + " id=" + book?.id)
        MyAlert.showAlertConfirm(myApplication.getText("do_delete"))
        countDownTimerConfirm = startConfirmTimer()
    }

    fun processRemoveItem() {
        myApplication?.myDatabase?.removeBookInMemory(book!!)
        myApplication?.myDatabase?.removeBookInDb(book!!)
        myApplication.homeAdapter?.updateAll()
    }

    fun startConfirmTimer(): CountDownTimer? {
        if (MyCommon.DEBUG_ALERT_LOG) {
            MyLogger.d("MyAlert - startCancelDialogTimer yesTapped=" + MyAlert.isAlertButtonYesTapped())
        }
        countDownTimerConfirm = object : CountDownTimer(
            MyCommon.DIALOG_PERIOD, MyCommon.TIME_TICK
        ) {
            override fun onTick(millisUntilFinished: Long) {
//                MyLogger.d("MyAlert - TICK startCancelDialogTimer yesTapped=" + MyAlert.isAlertButtonYesTapped())
                if (MyAlert.isAlertButtonYesTapped()) {
                    processRemoveItem()
                    stopConfirmTimer()
                }
                if (MyAlert.isAlertButtonNoTapped()) {
                    stopConfirmTimer()
                }
            }

            override fun onFinish() {
                if (MyCommon.DEBUG_ALERT_LOG) {
                    MyLogger.d("MyAlert - startCancelDialogTimer onFinish")
                }
                stopConfirmTimer()
            }
        }
        countDownTimerConfirm?.start()
        return countDownTimerConfirm
    }

    fun stopConfirmTimer() {
        countDownTimerConfirm?.cancel()
        countDownTimerConfirm = null
        MyAlert.cancelAlert(0)
    }


}