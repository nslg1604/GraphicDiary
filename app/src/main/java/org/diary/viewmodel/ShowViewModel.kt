package org.diary.viewmodel

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.model.Note
import org.diary.model.NoteShow
import org.diary.utils.MyLogger
import org.diary.ui.adapters.ShowAdapter
import org.diary.utils.MyConverter

/**
 * https://question-it.com/questions/314741/ispolzujte-knopku-gotovo-na-klaviature-v-privjazke-dannyh
 * https://stackoverflow.com/questions/59884516/edittext-imeoptions-and-databinding-not-working
 *
 */
class ShowViewModel constructor(fragment: Fragment, imageView1: ImageView) {
    val myApplication: MyApplication = MyApplication.instance!!
    val myDatabase = myApplication?.myDatabase

    var fragment: Fragment = fragment
    var title: String? = null
    var bookParent: Book? = null
    var bookChild: Book? = null
    var myDraw: MyDraw? = null
    var imageView: ImageView = imageView1
    var stepIndex = 0
    var genre = 0  // index
    var showAdapter: ShowAdapter? = null
    var notes: MutableList<Note>? = null
    var notesShow: MutableList<NoteShow> = ArrayList()
    var currentType = MyCommon.TYPE_TABLE

    // Create a LiveData
    val liveBookName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveParentName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val liveImage: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    val liveStep: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val liveShowType: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val liveGenre: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        MyLogger.d("ShowViewModel - init")
        bookChild = MyApplication.instance?.bookCurrent
        myApplication.bookChild = bookChild

        MyLogger.d("ShowViewModel - init bookCurrent=" + bookChild?.name)
        if (bookChild != null) {
            bookParent = myDatabase?.findBookById(bookChild?.parentId!!)
            MyLogger.d("ShowViewModel - init bookParent=" + bookParent?.name)
            showAdapter = ShowAdapter(bookChild)

            MyLogger.d("ShowViewModel - init adapter=" + showAdapter)
            myApplication?.showAdapter = showAdapter

            defineBookTitles()
            liveStep.value = MyCommon.SHOW_STEPS[stepIndex]
            liveGenre.value = genre
            notes = myDatabase!!.readNotes(bookChild!!)
//            myDraw = MyDraw()
        }
    }

    fun gotoLeftParent() {
        MyLogger.d("ShowViewModel - gotoLeftParent bookParent=" + bookParent?.name)
        myDatabase?.debugShowBooks()
        var bookPreviousParent = myDatabase?.findPreviousParentBook(bookParent!!)
        MyLogger.d("ShowViewModel - gotoLeftParent - bookPreviousParent=" + bookPreviousParent?.name)
        if (bookPreviousParent == null) {
            MyLogger.d("ShowViewModel - gotoLeftParent - bookPreviousParent = null")
            return
        }

        var bookNextChild = myDatabase?.findFirstChildForParent(bookPreviousParent!!)
        MyLogger.d("ShowViewModel - gotoLeftParent - bookNextChild=" + bookNextChild?.name)
        if (bookNextChild != null) {
            MyLogger.d("ShowViewModel - gotoLeftParent - found=" + bookNextChild.name + " parent=" + bookNextChild.parentId)
            bookChild = bookNextChild
            myApplication.bookChild = bookChild
            bookParent = bookPreviousParent
            showDataNow(true)
        }
    }

    fun gotoRightParent() {
        MyLogger.d("ShowViewModel - gotoRightParent bookParent=" + bookParent?.name)
        myDatabase?.debugShowBooks()
        var bookNextParent = myDatabase?.findNextParentBook(bookParent!!)
        MyLogger.d("ShowViewModel - gotoRightParent - bookNextParent=" + bookNextParent?.name)
        if (bookNextParent == null) {
            MyLogger.d("ShowViewModel - gotoRightParent - bookNextParent = null")
            return
        }
        var bookNextChild = myDatabase?.findFirstChildForParent(bookNextParent!!)
        MyLogger.d("ShowViewModel - gotoRightParent - child=" + bookNextChild?.name + " parent=" + bookNextChild?.parentId)
        if (bookNextChild != null) {
            bookChild = bookNextChild
            myApplication.bookChild = bookChild
            bookParent = bookNextParent
            showDataNow(true)
        }
    }

    fun gotoLeftChild() {
        MyLogger.d("ShowViewModel - gotoLeftChild")
        myDatabase?.debugShowBooks()
        var book = myDatabase?.findPreviousChildBook(bookChild!!)
        MyLogger.d("ShowViewModel - gotoLeftChild - found=" + book?.name + " parent=" + book?.parentId)
        if (book != null) {
            bookChild = book
            myApplication.bookChild = bookChild
            bookParent = myDatabase?.findBookById(bookChild?.parentId!!)
            showDataNow(true)
        }
    }

    fun gotoRightChild() {
        MyLogger.d("ShowViewModel - gotoRightChild bookChild=" + bookChild?.name)
        myDatabase?.debugShowBooks()
        var book = myDatabase?.findNextChildBook(bookChild!!)
        MyLogger.d("ShowViewModel - gotoRightChild - found=" + book?.name + " parent=" + book?.parentId)
        if (book != null) {
            bookChild = book
            myApplication.bookChild = bookChild
            bookParent = myDatabase?.findBookById(bookChild?.parentId!!)
            showDataNow(true)
        }
    }

    fun showDataNow(changeBook: Boolean) {
        MyLogger.d("ShowViewModel - showDataNow type=" + currentType + " change=" + changeBook)
        if (notes == null || changeBook) {
            notes = myDatabase!!.readNotes(bookChild!!)
        }
        MyLogger.d("ShowViewModel - showDataNow notes.size=" + notes?.size)
        createNotesShow()
        MyLogger.d("ShowViewModel - showDataNow type=" + currentType + " size=" + notesShow.size)
        if (currentType == MyCommon.TYPE_TABLE) {
            myApplication.showAdapter?.update()
        } else {
            myDraw = MyDraw()
            if (notesShow.isEmpty()!!) {
                if (myDraw?.imageWidth!! > 0 && myDraw?.imageHeight!! > 0) {
                    liveImage.value = myDraw?.drawEmptyBitmap()
                }
            } else {
                if (bookChild?.icon?.isEmpty()!!) {
                    showImage()
                }
            }

            if (!bookChild?.icon?.isEmpty()!!) {
                showOtherImages()
            }
        }
        defineBookTitles()
    }

    fun showImage() {
        MyLogger.d("ShowViewModel - showImage - bookCurrent=" + bookChild?.name + " type=" + currentType + " imageView.height=" + imageView.height)
        if (currentType == MyCommon.TYPE_TABLE) {
            return
        }

        var dayBegin = 0
        var note = myDatabase?.readFirstNote(bookChild!!)
        if (note != null){
            dayBegin = note.day
        }

        myDraw?.prepare(
            getColor(bookChild!!),
            notesShow,
            MyCommon.SHOW_STEPS[stepIndex],
            imageView.height,
            dayBegin,
            currentType,
            false  // reuse
        )
        liveImage.value = myDraw?.drawBitmap()
    }

    fun showOtherImages() {
        MyLogger.d("ShowViewModel - showOtherImages - bookCurrent=" + bookChild?.name + " type=" + currentType + " imageView.height=" + imageView.height)
        if (currentType == MyCommon.TYPE_TABLE) {
            return
        }

        var dayBegin = defineFirstDay()
        MyLogger.d("ShowViewModel - showOtherImages - dayBegin=" + dayBegin)
        var reuseBitmap = false
        for (book in myApplication.myDatabase?.booksSelected!!) {
//            MyLogger.d("ShowViewModel - showOtherImages - process book=" + book.name + " icon=" + book.icon)
            if (book.icon.isEmpty()) {
                continue
            }
            notes = myDatabase?.readNotes(book)
            createNotesShow()
            if (notesShow.isEmpty()) {
                continue
            }
            MyLogger.d("ShowViewModel - showOtherImages - book=" + book.name)

            myDraw?.initData()
            myDraw?.prepare(
                getColor(book),
                notesShow,
                MyCommon.SHOW_STEPS[stepIndex],
                imageView.height,
                dayBegin,
                currentType,
                reuseBitmap
            )
            reuseBitmap = true
            liveImage.value = myDraw?.drawBitmap()
        }
    }

    fun defineFirstDay(): Int{
        var dayBegin = MyCommon.MAX_INT
        for (book in myApplication.myDatabase?.booksSelected!!) {
            MyLogger.d("ShowViewModel - defineFirstDay - process book=" + book.name + " icon=" + book.icon)
            if (book.icon.isEmpty()) {
                continue
            }
            var note = myApplication.myDatabase?.readFirstNote(book)
            MyLogger.d("ShowViewModel - defineFirstDay - first day=" + note?.day)
            if (note != null) {
                if (note.day < dayBegin){
                    dayBegin = note.day
                    MyLogger.d("ShowViewModel - defineFirstDay - min=" + dayBegin)
                }
            }
        }
        if (dayBegin == MyCommon.MAX_INT){
            dayBegin = 0
        }
        return dayBegin
    }

    fun getColor(book: Book): Int {
        if (book.icon.isEmpty()) {
            return MyApplication.instance?.resources!!
                .getColor(R.color.newText, null)
        }

        var colorNum = MyConverter.toInt(book.icon)
        MyLogger.d("ShowViewModel - getColor - colorNum=" + colorNum)
        if (colorNum >= MyCommon.COLORS.size) {
            colorNum -= MyCommon.COLORS.size
        }
        return MyCommon.COLORS[colorNum]
    }

    fun gotoLeftStep() {
        if (stepIndex <= 0) {
            return
        }
        stepIndex -= 1
        liveStep.value = MyCommon.SHOW_STEPS[stepIndex]
        showDataNow(false)
    }

    fun gotoRightStep() {
        if (stepIndex < MyCommon.SHOW_STEPS.size - 1) {
            stepIndex += 1
            liveStep.value = MyCommon.SHOW_STEPS[stepIndex]
            showDataNow(false)
        }
    }

    fun gotoLeftGenre() {
        if (genre <= 0) {
            return
        }
        genre -= 1
        liveGenre.value = genre
        showDataNow(false)
    }

    fun gotoRightGenre() {
        if (genre < MyCommon.GENRES.size - 1) {
            genre += 1
            liveGenre.value = genre
            showDataNow(false)
        }
    }

    fun defineBookTitles() {
        MyLogger.d("ShowViewModel - defineBookTitles bookParent=" + bookParent?.name)
        liveParentName.value = bookParent?.name
        liveBookName.value = bookChild?.name
//        liveParentName.value = myApplication.myDatabase
//            ?.findBookById(bookChild?.parentId!!)?.name
    }

    fun selectedTable() {
        currentType = MyCommon.TYPE_TABLE
        liveShowType.value = currentType
        showDataNow(false)
    }

    fun selectedGraph() {
        MyLogger.d("ShowViewModel - selectedGraph")
        currentType = MyCommon.TYPE_GRAPH
        liveShowType.value = currentType
        showDataNow(false)
    }

    fun selectedRect() {
        MyLogger.d("ShowViewModel - selectedRect")
        currentType = MyCommon.TYPE_RECT
        liveShowType.value = currentType
        showDataNow(false)
    }

    fun createNotesShow() {
        MyLogger.d("ShowViewModel - createNotesShow notes.size=" + notes?.size)
        if (notes == null || notes?.isEmpty()!!) {
            myApplication.notes = notes
            return
        }
        if (genre == MyCommon.GENRE_AVERAGE) {
            createNotesAverage()
        }

        if (genre == MyCommon.GENRE_FREQUENCY) {
            createNotesFrequency()
        }

        if (genre == MyCommon.GENRE_INCREASING) {
            createNotesIncreasing()
        }

        if (currentType == MyCommon.TYPE_TABLE) {
            var notesUpdated: MutableList<Note> = ArrayList()
            for (notesShow in notesShow) {
                var myValue = ""
                myValue = "%.1f".format(notesShow.value!!)
//                if (bookChild?.type == MyCommon.VALUE_TYPE_FLOAT) {
////                    myValue = "%.1f".format(notesShow.value!!)
//                    myValue = String.format("%.1f", notesShow.value!!)
//                }
//                else if (bookChild?.type == MyCommon.VALUE_TYPE_INTEGER) {
//                    myValue = notesShow.value!!.toInt().toString()
//                }
                notesUpdated.add(Note(bookChild?.id!!, notesShow.day, myValue))
            }
            myApplication.notes = notesUpdated
            MyLogger.d("ShowViewModel - createNotesShow notesUpdated.size=" + notesUpdated.size)
        }
    }

    fun createNotesAverage() {
        notesShow = ArrayList()
        if (stepIndex == 0) {
            for (note in notes!!) {
                notesShow.add(NoteShow(note.day, MyConverter.toFloat(note.value)))
            }
            return
        }
        var stepCount = 0
        var average: Float = 0f

        var dayLast = 0
        var i = 0
        for (note in notes!!) {
            average += MyConverter.toFloat(note.value)
            MyLogger.d("ShowViewModel - createNotesShow day=" + note.day + " value=" + note.value + " stepCount=" + stepCount + " average=" + average)

            if (++stepCount >= MyCommon.SHOW_STEPS[stepIndex]) {
                MyLogger.d("ShowViewModel - createNotesShow day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + MyCommon.SHOW_STEPS[stepIndex])
                notesShow.add(NoteShow(note.day, average / MyCommon.SHOW_STEPS[stepIndex]))
                dayLast = note.day
                stepCount = 0
                average = 0f
                continue
//                if (++i >= notes?.size!!) {
//                    break
//                }
            }
            if (++i >= notes?.size!! && dayLast != note.day) {
                MyLogger.d("ShowViewModel - createNotesShow day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + MyCommon.SHOW_STEPS[stepIndex])
                notesShow.add(NoteShow(note.day, average / stepCount))
            }
        }
    }

    fun createNotesIncreasing() {
        notesShow = ArrayList()
        var stepCount = 0
        var average = 0f
        var i = 0
        var dayLast = 0
        for (note in notes!!) {
            average += MyConverter.toFloat(note.value)
            MyLogger.d("ShowViewModel - createNotesIncreasing day=" + note.day + " value=" + note.value + " stepCount=" + stepCount + " average=" + average)
            if (++stepCount >= MyCommon.SHOW_STEPS[stepIndex]) {
                MyLogger.d("ShowViewModel - createNotesIncreasing day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + MyCommon.SHOW_STEPS[stepIndex])
                notesShow.add(NoteShow(note.day, average))
                stepCount = 0
                dayLast = note.day
            }
            if (++i >= notes?.size!! && dayLast != note.day) {
                MyLogger.d("ShowViewModel - createNotesIncreasing day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + MyCommon.SHOW_STEPS[stepIndex])
                notesShow.add(NoteShow(note.day, average))
            }
        }
    }

    fun createNotesFrequency() {
        notesShow = ArrayList()
        if (notes?.size == 1) {
            notesShow.add(NoteShow(notes?.get(0)?.day!!, 0f))
            return
        }

        var average = 0f
        var dayLast = -999
        var i = 1
        var stepCount = 1
        for (note in notes!!) {
            // first note
            if (dayLast < 0) {
                dayLast = note.day
                continue
            }
            average += note.day - dayLast
            dayLast = note.day
            MyLogger.d("ShowViewModel - createNotesFrequency loop day=" + note.day + " value=" + note.value + " stepCount=" + stepCount + " average=" + average)
            if (++stepCount >= MyCommon.SHOW_STEPS[stepIndex]) {
                MyLogger.d("ShowViewModel - createNotesFrequency end step day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + stepIndex)
                notesShow.add(NoteShow(note.day, average / MyCommon.SHOW_STEPS[stepIndex]))
                stepCount = 0
                average = 0f
//                if (++i >= notes?.size!!) {
//                    MyLogger.d("ShowViewModel - createNotesFrequency break")
//                    break
//                }
            }
            MyLogger.d("ShowViewModel - createNotesFrequency day=" + note.day + " i=" + i)
            if (++i >= notes?.size!! && dayLast != note.day) {
                MyLogger.d("ShowViewModel - createNotesFrequency the last day=" + note.day + " average=" + average + " stepCount=" + stepCount + " value=" + (average / 2) + " stepDays=" + MyCommon.SHOW_STEPS[stepIndex])
                notesShow.add(NoteShow(note.day, average / stepCount))
            }

        }
    }

    fun getLeft(): String {
//        return "&lt;"
        return "A"
    }

}