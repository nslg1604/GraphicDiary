package org.diary.viewmodel

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.diary.common.MyApplication
import org.diary.model.Book
import org.diary.ui.fragments.HomeFragment
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import java.util.*


/**
 * https://question-it.com/questions/314741/ispolzujte-knopku-gotovo-na-klaviature-v-privjazke-dannyh
 * https://stackoverflow.com/questions/59884516/edittext-imeoptions-and-databinding-not-working
 *
 */
class ValueViewModel constructor(fragment: Fragment){
    var topLayout:View ?= null
    var parentName: String ?= null
    val myApplication: MyApplication = MyApplication.instance!!
    var fragment: Fragment = fragment
    var requestNumber = ""
    var linesNumber: Int ?= 1
    var title: String = ""
    var myText =  ObservableField<String>()
    var bookCurrent: Book ?= null
//    var editText: View ?= null

    // Create a LiveData
    val liveEdit: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        MyLogger.d("ValueViewModel - init bookCurrent=" + myApplication?.bookCurrent?.name)
        myText?.set("")

        // get params if add new category
        bookCurrent = myApplication?.bookCurrent
        if (bookCurrent == null) {
            title = myApplication.getText("new_category")
        }
        else {
            title = myApplication.getText("change")
            liveEdit.value = bookCurrent?.name
        }
    }

    fun addTitle(): String {
        MyLogger.d("ValueViewModel - addTitle")
        return title!!
    }

    fun onEditorAction(view:View, actionId: Int, event: KeyEvent?): Boolean?{
        MyLogger.d("ValueViewModel - onEditorAction actionId=$actionId event=$event")
        var keyCode = event?.keyCode
        MyLogger.d("ValueViewModel - CLICK keyCode=$keyCode")
//        editText = view

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            MyLogger.d("ValueViewModel - DONE")
            if (myText?.get().toString().isEmpty()){
                return true
            }
            MyLogger.d("ValueViewModel - ready=" + myText?.get().toString())
//            clickReady()
            return true
        }
        return false
    }

    fun setTextReady(): String{
        return myApplication.getText("ok")
    }

    fun setTextCancel(): String{
        return myApplication.getText("cancel")
    }

    fun clickCancel(){
        MyUtils.changeFragment(fragment, HomeFragment(), null)
    }

    fun View.hideKeyboard(): Boolean {
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        } catch (ignored: RuntimeException) { }
        return false
    }

    fun clickReady(){
        MyLogger.d("ValueViewModel - clickReady myApplication?.bookCurrent=" + myApplication?.bookCurrent?.name + " myText=" + myText?.get().toString())
        if (myText?.get().toString().isEmpty()){
            return
        }

//        MyUtils.hideKeyboard(myApplication.editText!!)
        if (myApplication?.bookCurrent == null){
            // create new book
            var book = Book(myApplication.nextBookId(),
            Book.LEVEL_PARENT,
            myText.get().toString(),
            "",  // parent
            "", 0, 0f, 0f)
            myApplication.myDatabase?.addBook(book)  // new
        }
        // change category
        else {
            myApplication?.bookCurrent?.name = myText.get().toString()
            myApplication.myDatabase?.addBook(myApplication?.bookCurrent!!)  // update
        }
        MyUtils.waitGotoHome(fragment)
//        MyUtils.changeFragment(fragment, HomeFragment(), null)

    }

}