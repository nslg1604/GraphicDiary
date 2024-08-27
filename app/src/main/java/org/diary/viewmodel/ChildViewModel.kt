package org.diary.viewmodel

import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.ui.fragments.HomeFragment
import org.diary.utils.MyConverter
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils


/**
 * https://question-it.com/questions/314741/ispolzujte-knopku-gotovo-na-klaviature-v-privjazke-dannyh
 * https://stackoverflow.com/questions/59884516/edittext-imeoptions-and-databinding-not-working
 *
 */
class ChildViewModel(fragment: Fragment){

    val myApplication: MyApplication = MyApplication.instance!!
    var fragment: Fragment = fragment
    var textChild =  ObservableField<String>()
    var textMin =  ObservableField<String>()
    var textMax =  ObservableField<String>()
    var valueType = MyCommon.VALUE_TYPE_INTEGER
    var bookParent = myApplication?.bookParent
    var bookChild = myApplication?.bookChild

    // Create a LiveData
    val liveEdit: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveType: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val liveMin: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    val liveMax: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    init {
        MyLogger.d("ChildViewModel - init bookChild=" + myApplication?.bookChild)
        textChild.set("")
        textMin.set("")
        textMax.set("")

        // edit existing child
        if (bookChild != null){
            textChild.set(bookChild?.name)
            valueType = bookChild!!.type
            liveType.value = valueType

            if (valueType == MyCommon.VALUE_TYPE_INTEGER) {
                textMin.set(bookChild!!.min.toInt().toString())
                textMax.set(bookChild!!.max.toInt().toString())
            }
            else {
                textMin.set(bookChild!!.min.toString())
                textMax.set(bookChild!!.max.toString())
            }


        }
    }


    fun addTitle(): String {
//        MyLogger.d("ChildViewModel - addTitle")
        if (bookChild == null) {
            return myApplication.getText("new_element") +
                    "\"" + bookParent?.name + "\""
        }
        // existing child
        else {
            return myApplication.getText("change")
        }
    }

//    fun setTitleEdit(): String{
//        var text = ""
//        if (bookChild != null){
//            text = bookChild?.name!!
//        }
//        return text
//    }

    fun gotoLeftType(){
        if (valueType > 1) {
            valueType -= 1
            liveType.value = valueType
        }
    }

    fun gotoRightType(){
        if (valueType < MyCommon.VALUE_TYPE_MAX) {
            valueType += 1
            liveType.value = valueType
        }
    }

    fun setTextReady(): String{
        return myApplication.getText("ok")
    }

    fun setTextCancel(): String{
        return myApplication.getText("cancel")
    }

    fun setTextMin(): String{
        return myApplication.getText("min")
    }

    fun setTextMax(): String{
        return myApplication.getText("max")
    }

    fun clickReady(){
        MyLogger.d("ChildViewModel - clickReady current=" + myApplication?.bookCurrent?.name + " edit=" + textChild?.get())
        if (textChild.get()!!.isEmpty()){
            return
        }

        var bookChildId: String ?= null
        if (bookChild == null){
            bookChildId = MyApplication!!.instance!!.nextBookId()
        }
        else {
            bookChildId = bookChild?.id
        }

        // New book
        var book = Book(
                bookChildId!!,
                Book.LEVEL_CHILD,
                textChild.get()!!,
                bookParent?.id!!,
                "",  // icon
                valueType,
                MyConverter.toFloat(textMin.get()!!),
                MyConverter.toFloat(textMax.get()!!)
            )
        myApplication?.bookChild = book
        myApplication?.bookCurrent = book
        myApplication?.myDatabase?.addBook(book)  // add child
        MyUtils.waitGotoHome(fragment)
    }

    fun clickCancel(){
        MyLogger.d("ChildViewModel - clickCancel current=" + myApplication?.bookCurrent?.name + " edit=" + textChild?.get())
        MyUtils.changeFragment(fragment, HomeFragment(), null)
    }

}