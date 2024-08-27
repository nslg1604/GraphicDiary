package org.diary.viewmodel

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.model.Note
import org.diary.utils.MyCalendar
import org.diary.utils.MyConverter
import org.diary.utils.MyLogger
import java.util.*


/**
 * https://question-it.com/questions/314741/ispolzujte-knopku-gotovo-na-klaviature-v-privjazke-dannyh
 * https://stackoverflow.com/questions/59884516/edittext-imeoptions-and-databinding-not-working
 *
 */
class FillViewModel constructor(fragment: Fragment) {

    companion object {
        val REQUEST_NUMBER: String = "REQUEST_NUMBER"
        val LINES_NUMBER: String = "LINES_NUMBER"
        val RESULT_STRING: String = "RESULT_STRING"
    }

    var parentName: String? = null
    val myApplication: MyApplication = MyApplication.instance!!
    val myDatabase = myApplication?.myDatabase
    var fragment: Fragment = fragment
    var editTextView: View? = null
    var requestNumber = ""
    var linesNumber: Int? = 0
    var title: String? = null
    var myEditText = ObservableField<String>()
    var currentCalendar = Calendar.getInstance()
    var bookParent: Book ?= null
    var bookChild: Book? = null
    var isValid: Boolean = false

    // Create a LiveData
    val currentDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveBookName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveParentName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveNoteValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveNoteColor: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val liveEditDrawable: MutableLiveData<Drawable> by lazy {
        MutableLiveData<Drawable>()
    }

    init {
        initData()
//        imageView.post(Runnable {
//            MyLogger.d("FillViewModel - init - width=" + imageView.width + " height=" + imageView.height)
//            showImage()
//        })

    }

    fun initData() {
        bookChild = myApplication?.bookCurrent
        MyLogger.d("FillViewModel - initData bookChild=" + bookChild?.name)

        if (bookChild == null) {
            bookChild = myApplication?.myDatabase?.findFirstChildBook()
            if (bookChild == null) {
                MyLogger.e("FillViewModel - initData - no books")
                return
            }
        }
        bookParent = myApplication?.myDatabase?.findBookById(bookChild?.parentId!!)
        if (bookParent == null){
            MyLogger.s("FillViewModel - FATAL - bookParent=null")
            return
        }
        MyLogger.d("FillViewModel - initData name=" + bookChild?.name + " lineNumbers=$linesNumber")
        defineBookTitles()
        changeEditText()
    }

    fun gotoLeftParent(){
        MyLogger.d("FillViewModel - gotoLeftParent bookParent=" + bookParent?.name)
        myDatabase?.debugShowBooks()
        if (bookParent?.name == null){
            return
        }
        var bookPreviousParent = myDatabase?.findPreviousParentBook(bookParent!!)
        MyLogger.d("FillViewModel - gotoLeftParent - bookPreviousParent=" + bookPreviousParent?.name)
        if (bookPreviousParent == null){
            MyLogger.d("FillViewModel - gotoLeftParent - bookPreviousParent = null")
            return
        }

        var bookNextChild = myDatabase?.findFirstChildForParent(bookPreviousParent!!)
        MyLogger.d("FillViewModel - gotoLeftParent - bookNextChild=" + bookNextChild?.name)
        if (bookNextChild != null){
            MyLogger.d("FillViewModel - gotoLeftParent - found=" + bookNextChild.name + " parent=" + bookNextChild.parentId)
            bookChild = bookNextChild
            bookParent = bookPreviousParent
            defineBookTitles()
            changeEditText()
        }
    }

    fun gotoRightParent(){
        MyLogger.d("FillViewModel - gotoRightParent bookParent=" + bookParent?.name)
        myDatabase?.debugShowBooks()
        var bookNextParent = myDatabase?.findNextParentBook(bookParent!!)
        MyLogger.d("FillViewModel - gotoRightParent - bookNextParent=" + bookNextParent?.name)
        if (bookNextParent == null){
            MyLogger.d("FillViewModel - gotoRightParent - bookNextParent = null")
            return
        }
        var bookNextChild = myDatabase?.findFirstChildForParent(bookNextParent!!)
        MyLogger.d("FillViewModel - gotoRightParent - found=" + bookNextChild?.name + " parent=" + bookNextChild?.parentId)
        if (bookNextChild != null){
            bookChild = bookNextChild
            bookParent = bookNextParent
            defineBookTitles()
            changeEditText()
        }
    }


    fun defineBookTitles() {
        liveBookName.value = bookChild?.name!!
//        currentParentName.value = currentBook?.parentId!!
        liveParentName.value = myApplication.myDatabase
            ?.findBookById(bookChild?.parentId!!)?.name
    }

    fun gotoLeftChild() {
        MyLogger.d("FillViewModel - gotoLeftBook")
        saveNote()
        var book = myDatabase?.findPreviousChildBook(bookChild!!)
        if (book != null) {
            MyLogger.d("FillViewModel - gotoLeftBook - found=" + book.name + " parent=" + book.parentId)
            bookChild = book
            myApplication.bookCurrent = book
            bookParent = myDatabase?.findBookById(bookChild?.parentId!!)
            defineBookTitles()
            changeEditText()
        } else {
//            MyLogger.d("FillViewModel - gotoLefttBook - NO more books")
            liveNoteValue.value = ""
        }
    }

    fun gotoRightChild(): Boolean {
        MyLogger.d("FillViewModel - gotoRightBook")
        saveNote()
        var book = myDatabase?.findNextChildBook(bookChild!!)
        if (book != null) {
            MyLogger.d("FillViewModel - gotoRightBook - found=" + book.name + " parent=" + book.parentId)
            bookChild = book
            myApplication.bookCurrent = book
            bookParent = myDatabase?.findBookById(bookChild?.parentId!!)
            defineBookTitles()
            changeEditText()
            return true
        } else {
//            MyLogger.d("FillViewModel - gotoRightBook - NO more books")
            liveNoteValue.value = ""
            return false
        }
    }

    fun gotoLeftDate() {
        MyLogger.d("FillViewModel - gotoLeftDate")
        saveNote()
        currentCalendar = MyCalendar.getCalendarYesterday(currentCalendar)
        currentDate.value = MyCalendar.calendarToDateDD_MM_YYYY(currentCalendar)
        changeEditText()
    }

    fun gotoRightDate() {
        MyLogger.d("FillViewModel - gotoRightDate")
        saveNote()
        currentCalendar = MyCalendar.getCalendarTomorrow(currentCalendar)
        currentDate.value = MyCalendar.calendarToDateDD_MM_YYYY(currentCalendar)
        changeEditText()
    }

    fun changeEditText() {
        var day = MyCalendar.calendarToDay(currentCalendar)
        MyLogger.d(
            "FillViewModel - changeEditText - book=" + bookChild?.name + " date=" + MyCalendar.dayToDate(
                day,
                MyCalendar.DATE_TYPE_DDMMYYYY
            ) + " day=" + day
        )
        myApplication.myDatabase?.showAllNotes(bookChild!!)
        var note = myApplication.myDatabase?.findNoteByDay(bookChild?.id!!, day)
        if (note != null) {
            if (note.value.equals("null")){
                note.value = ""
            }
            MyLogger.d("FillViewModel - changeEditText - found=" + note.id + " value=" + note.value)
            liveNoteValue.value = note.value
        } else {
            MyLogger.d("FillViewModel - changeEditText - note not found")
            liveNoteValue.value = ""
        }
    }

    fun getDate(): String? {
        currentDate.value = MyCalendar.calendarToDateDD_MM_YYYY(currentCalendar)
        return currentDate.value
    }

    fun onEditorAction(view: View, actionId: Int, event: KeyEvent?): Boolean? {
//        MyLogger.d("FillViewModel - onEditorAction actionId=$actionId event=$event")
        editTextView = view

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            saveNote()
//            myEditText?.set("")
//            if (!gotoRightChild()) {
//            }
            return true
        }
        return false
    }


    fun myTextWatcher(): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                MyLogger.d("FillViewModel - myTextWatcher text=" + charSequence.toString())
//                MyLogger.d("FillViewModel - myTextWatcher text=" + charSequence.toString() + " i=$i i1=$i2 i2=$i2")
//                if (charSequence.toString().contains("\n")){
//                    MyLogger.d("FillViewModel - FOUND ENTER")
//                }
            }

            override fun afterTextChanged(editable: Editable) {
                MyLogger.d("FillViewModel - afterTextChanged text=" + editable.toString() + " myText=" + myEditText?.get())
                isValid = false
                if (changeEditColor()){
                    isValid = true
                }
            }
        }
    }

    fun changeEditColor(): Boolean {
        MyLogger.d("FillViewModel - changeEditColor type=" + bookChild?.type)
        if (bookChild?.type == MyCommon.VALUE_TYPE_TEXT ||
            bookChild?.min?.toInt()!! == bookChild?.max?.toInt()!! ) {
            return true
        }
        var str = myEditText?.get().toString()
        var regex = MyCommon.REGEX_INTEGER
        if (bookChild?.type == MyCommon.VALUE_TYPE_FLOAT) {
            regex = MyCommon.REGEX_FLOAT
        }
        if (str.isEmpty()) {
//            liveNoteColor.value = myApplication.getColor(R.color.newWhite)
            liveNoteColor.value = myApplication.getColor(R.color.colorLightLightGray)
            liveEditDrawable.value = myApplication.getDrawable(R.drawable.rectangle_edit)
            return false
        }
        if (regex.matches(str)) {
            if (bookChild?.type == MyCommon.VALUE_TYPE_INTEGER) {
                if (str.toInt() < bookChild?.min?.toInt()!! ||
                    str.toInt() > bookChild?.max?.toInt()!!){
                    liveNoteColor.value = myApplication.getColor(R.color.editTextBkgWrong)
                    liveEditDrawable.value = myApplication.getDrawable(R.drawable.rectangle_wrong)
                    return false
                }
            }
            if (bookChild?.type == MyCommon.VALUE_TYPE_FLOAT) {

                var myValue = MyConverter.toFloat(str)
                if (myValue < bookChild?.min!! ||
                    myValue > bookChild?.max!!){
                    liveNoteColor.value = myApplication.getColor(R.color.editTextBkgWrong)
                    liveEditDrawable.value = myApplication.getDrawable(R.drawable.rectangle_wrong)
                    return false
                }
            }
//            MyLogger.d("FillViewModel - changeEditColor VALID")
            liveNoteColor.value = myApplication.getColor(R.color.editTextBkgValid)
            liveEditDrawable.value = myApplication.getDrawable(R.drawable.rectangle_good)
            return true
        } else {
//            MyLogger.d("FillViewModel - changeEditColor WRONG")
            liveNoteColor.value = myApplication.getColor(R.color.editTextBkgWrong)
            liveEditDrawable.value = myApplication.getDrawable(R.drawable.rectangle_wrong)
            return false
        }
        return false
    }

    fun saveNote() {
        if (isValid) {
            var str = myEditText?.get().toString().replace(",", ".")
            if (!str.isEmpty()) {
                var day: Int = MyCalendar.calendarToDay(currentCalendar)
                myDatabase?.saveNote(
                    Note(bookChild?.id!!, day, str)
                )
            }
        }
    }


//    fun returnToCaller(view: View, result: String) {
//        MyLogger.d("FillViewModel - returnToCaller string=" + result + " parentName=" + parentName)
//        val bundle = Bundle()
//        bundle.putString(RESULT_STRING, result)
////        if (parentName != null) {
////            bundle.putString(PARENT_NAME, parentName)
////        }
//        MyUtils.hideKeyboard(view)
//        fragment.setFragmentResult(requestNumber, bundle)
//        fragment.parentFragmentManager.popBackStack()
//    }

    fun onBackPressed() {
        MyLogger.d("FillViewModel - onBackPressed")
        saveNote()
    }

}