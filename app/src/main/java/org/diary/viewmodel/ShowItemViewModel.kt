package org.diary.viewmodel

import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.ui.fragments.ShowFragment
import org.diary.model.Note
import org.diary.utils.MyCalendar
import org.diary.utils.MyConverter
import org.diary.utils.MyLogger

class ShowItemViewModel constructor(showFragment: ShowFragment) {
    private var showFragment: ShowFragment = showFragment
    private var note: Note? = null
    var day = ""
    var myValue = ""
    private var myApplication: MyApplication = MyApplication.instance!!

    fun init() {
        MyLogger.d("ShowItemViewModel - constructor")
    }

    fun bind(note: Note) {
        MyLogger.d("ShowItemViewModel - bind - note=" + note.day)
        this.note = note
        day = MyCalendar.dayToDate(note.day, MyCalendar.DATE_TYPE_DDMMYYYY)
        myValue = note.value!!.replace(",", ".")
    }

    fun getValue(): String {
        return myValue
    }

}