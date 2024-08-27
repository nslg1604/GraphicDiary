package org.diary.model

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Environment
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.utils.MyConverter
import org.diary.utils.MyFileUtils
import org.diary.utils.MyLogger
import org.diary.viewmodel.BookViewModel
import java.io.File


class MyDatabase constructor(isMain: Boolean) {
    companion object {
        val TABLE_BOOKS = "books"

        val HEALTH = "health"
        val WEIGHT = "weight"
        val SPORT = "sport"
        val BIKE = "bike"
        val POINTS = "bike_points"
        val CAR = "car"
        val FUEL = "car_fuel"
    }

    private var db: SQLiteDatabase? = null
    private var myApplication = MyApplication.instance
    var databasePath: String? = null
    private var isMain = isMain

    //    private var context: Context ?= null
    private val busy = false
    private val isOpened = false

    val MAX_FLOAT = 9999999f
    private var books: MutableList<Book> = ArrayList()

    //    private var notes: MutableList<Note> = ArrayList()
    var firstLowBook: Book? = null
    var id = 1
    var booksSelected: MutableList<Book> = java.util.ArrayList()

    init {
        MyLogger.d("MyDatabase - init isMain=" + isMain)
        myApplication = MyApplication.instance

        if (MyCommon.EXT_STORAGE) {
            var databaseDir =
                Environment.getExternalStoragePublicDirectory(MyCommon.DB_DIR).toString()
            var file = File(databaseDir)
            file.mkdirs()
            databasePath = databaseDir + "/" + MyCommon.DB_NAME
        } else {
            if (isMain) {
                databasePath = myApplication!!.getDatabasePath(MyCommon.DB_NAME).absolutePath
            } else {
                var databaseDir =
                    Environment.getExternalStoragePublicDirectory(MyCommon.DB_DIR).toString()
                var file = File(databaseDir)
                file.mkdirs()
                databasePath = databaseDir + "/" + MyCommon.DB_NAME
                MyLogger.d(
                    "MyDatabase - init isMain=" + isMain + " dir=" + databaseDir + " exists=" + MyFileUtils.isFileExist(
                        databaseDir!!
                    ) + " path=" + databasePath
                )
            }
        }


        MyLogger.d(
            "MyDatabase - init isMain=" + isMain + " dir=" + databasePath + " exists=" + MyFileUtils.isFileExist(
                databasePath!!
            )
        )
    }

    fun createTestData() {
        MyLogger.d("MyDatabase - createTestData")
        createTestBooks()
        createTestNotes()
    }

    fun createTestBooks() {
        var booksInit: MutableList<Book> = ArrayList()

        // 2
        var idHealth = myApplication?.nextBookId()!!
        booksInit.add(
            Book(
                idHealth, Book.LEVEL_PARENT,
                myApplication?.getText(HEALTH)!!, "", "", 0, 0f, 0f
            )
        )
        booksInit.add(
            Book(
                myApplication?.nextBookId()!!,
                Book.LEVEL_CHILD,
                myApplication?.getText(WEIGHT)!!,
                idHealth,
                "",
                MyCommon.VALUE_TYPE_INTEGER,
                60f, +80f
            )
        )
        // 3
        var idSport = myApplication?.nextBookId()!!
        booksInit.add(
            Book(
                idSport, Book.LEVEL_PARENT,
                myApplication?.getText(SPORT)!!, "", "", 0, 0f, 0f
            )
        )
        booksInit.add(
            Book(
                myApplication?.nextBookId()!!,
                Book.LEVEL_CHILD,
                myApplication?.getText(BIKE)!!,
                idSport,
                "",
                MyCommon.VALUE_TYPE_FLOAT,
                0f,
                100f
            )
        )
        booksInit.add(
            Book(
                myApplication?.nextBookId()!!,
                Book.LEVEL_CHILD,
                myApplication?.getText(POINTS)!!,
                idSport,
                "",
                MyCommon.VALUE_TYPE_INTEGER,
                1f,
                5f
            )
        )

        MyLogger.d("MyDatabase - createTestBooks all=" + booksInit.size)
//        showAllBooks()

        // Add to db
        for (bookInit in booksInit) {
            addBook(bookInit)  // test
        }
    }

    fun createTestNotes() {
        MyLogger.d("MyDatabase - createTestNotes")
        var notesTest: MutableList<Note> = ArrayList()

        // Points
        var bookEst = findBookByName(myApplication?.getText(POINTS)!!)
        if (bookEst == null) {
            return
        }
        var idEst = bookEst?.id
        notesTest.add(Note(idEst, 3, "5"))
        notesTest.add(Note(idEst!!, 4, "3"))
        notesTest.add(Note(idEst!!, 6, "2"))
        notesTest.add(Note(idEst!!, 7, "4"))
        notesTest.add(Note(idEst!!, 8, "1"))
        notesTest.add(Note(idEst!!, 9, "3"))
        notesTest.add(Note(idEst!!, 10, "2"))
        notesTest.add(Note(idEst!!, 11, "2"))
        notesTest.add(Note(idEst!!, 12, "4"))
        notesTest.add(Note(idEst!!, 13, "3"))
        notesTest.add(Note(idEst!!, 14, "3"))
        notesTest.add(Note(idEst!!, 15, "1"))
        notesTest.add(Note(idEst!!, 17, "1"))
        notesTest.add(Note(idEst!!, 18, "2"))
        notesTest.add(Note(idEst!!, 19, "5"))
        notesTest.add(Note(idEst!!, 20, "3"))
        notesTest.add(Note(idEst!!, 21, "2"))
        notesTest.add(Note(idEst!!, 22, "1"))
        notesTest.add(Note(idEst!!, 23, "2"))
        notesTest.add(Note(idEst!!, 24, "4"))
        notesTest.add(Note(idEst!!, 25, "4"))
        notesTest.add(Note(idEst!!, 26, "4"))
        notesTest.add(Note(idEst!!, 27, "3"))
        notesTest.add(Note(idEst!!, 28, "4"))
        notesTest.add(Note(idEst!!, 29, "1"))
        notesTest.add(Note(idEst!!, 30, "5"))

        // Weight
        var bookWeight = findBookByName(myApplication?.getText(WEIGHT)!!)
        if (bookWeight == null) {
            return
        }
        var idWeight = bookWeight?.id
        notesTest.add(Note(idWeight, 2, "70"))
        notesTest.add(Note(idWeight, 9, "70.2"))
        notesTest.add(Note(idWeight, 15, "70.3"))
        notesTest.add(Note(idWeight, 25, "70.5"))
        notesTest.add(Note(idWeight, 32, "70.1"))
        notesTest.add(Note(idWeight, 45, "69.8"))
        notesTest.add(Note(idWeight, 60, "70.1"))

        // Add to db
        for (notesTest in notesTest) {
            var book = findBookById(notesTest.id)
            if (book != null) {
                saveNote(notesTest)  // test
            }
        }

        // Bike
        var bookBike = findBookByName(myApplication?.getText(BIKE)!!)
        if (bookBike == null) {
            return
        }
        var idBike = bookBike?.id
        notesTest.add(Note(idBike, 2, "15"))
        notesTest.add(Note(idBike!!, 4, "10"))
        notesTest.add(Note(idBike!!, 11, "16"))
        notesTest.add(Note(idBike!!, 16, "14"))
        notesTest.add(Note(idBike!!, 18, "20"))
        notesTest.add(Note(idBike!!, 32, "11"))
        notesTest.add(Note(idBike!!, 35, "6"))
        notesTest.add(Note(idBike!!, 36, "8"))
        notesTest.add(Note(idBike!!, 55, "12"))
        notesTest.add(Note(idBike!!, 56, "12"))
        notesTest.add(Note(idBike!!, 60, "3"))
        notesTest.add(Note(idBike!!, 64, "33"))
        notesTest.add(Note(idBike!!, 72, "11"))
        notesTest.add(Note(idBike!!, 73, "15"))
        notesTest.add(Note(idBike!!, 90, "25"))

        // Add to db
        for (notesTest in notesTest) {
            var book = findBookById(notesTest.id)
            if (book != null) {
                saveNote(notesTest)  // test
            }
        }
    }

    fun removeBookInMemory(bookRemoving: Book): Boolean? {
        MyLogger.d("MyDatabase - removeBookInMemory book=" + bookRemoving.name + " id=" + bookRemoving.id)
        for (book: Book in books) {
            if (bookRemoving.id.equals(book.id)) {
                books.remove(book)
                return true
            }
        }
        return false
    }

    fun findBookByName(name: String): Book? {
        for (book: Book in books) {
            if (name.equals(book.name)) {
                return book
            }
        }
        MyLogger.e("MyDatabase - findByName - NOT FOUND book=" + name)
        return null
    }

    fun findBookById(id: String): Book? {
        for (book: Book in books) {
            if (id.equals(book.id)) {
                return book
            }
        }
        MyLogger.e("MyDatabase - findByName - NOT FOUND book=" + id)
        return null
    }

    fun prepareAll(): MutableList<Book> {
//        books.clear()
        if (books.isEmpty()) {
            books = readBooks()!!
        }
        MyLogger.d("MyDatabase - prepareAll  size=" + books.size)
        booksSelected = java.util.ArrayList()
        for (book: Book in books) {
            if (book.level == Book.LEVEL_PARENT) {
//                MyLogger.d("MyDatabase - prepareAll " + book.name + " level=" + book.level + " expanded=" + book.expanded)
                addExpandedChilds(booksSelected, book)
            }
        }
//        showSelectedBooks(booksSelected)
        return booksSelected
    }

    fun addExpandedChilds(booksSelected: MutableList<Book>, bookParent: Book):
            MutableList<Book> {
//        MyLogger.d("MyDatabase - addExpandedChilds parent=" + bookParent.name + " expanded=" + bookParent?.expanded)
        booksSelected.add(bookParent)
        if (bookParent.expanded) {
            for (bookChild: Book in books) {
                if (bookChild.level - 1 == bookParent.level) {
                    if (bookChild.parentId.equals(bookParent.id)) {
                        addExpandedChilds(booksSelected, bookChild)
                    }
                }
            }
        }
        return booksSelected
    }

    fun findElementByIndex(bookFind: Book): Int {
        for (i in 0 until books!!.size) {
            var book: Book = books.get(i)
            if (bookFind.name.equals(book.name)) {
                return i
            }
        }
        return -1
    }

    fun findElementByName(nameToFind: String): Book? {
        for (i in 0 until books!!.size) {
            var book: Book = books.get(i)
            if (nameToFind.equals(book.name)) {
                return book
            }
        }
        return null
    }

    fun findFirstChildForParent(bookParent: Book): Book? {
        for (book in booksSelected) {
            if (book.level.equals(Book.LEVEL_CHILD) &&
                book.parentId.equals(bookParent.id)
            ) {
                return book
            }
        }
        return null
    }

    fun findFirstChildBook(): Book? {
        for (book in booksSelected) {
            if (book.level.equals(Book.LEVEL_CHILD)) {
                firstLowBook = book
                return book
            }
        }
        return null
    }

    fun findPreviousParentBook(bookParent: Book): Book? {
        if (bookParent == null) {
            return null
        }
        var bookPrevious: Book? = null
//        MyLogger.d("MyDatabase - findPreviousParentBook bookParent=" + bookParent.name + " id=" + bookParent.id)
        for (book in booksSelected) {
//            MyLogger.d("MyDatabase - findPreviousParentBook loop name=" + book.name + " id=" + book.id + " level=" + book.level)
            if (book.level.equals(Book.LEVEL_PARENT)) {
                if (book.id.equals(bookParent.id)) {
                    return bookPrevious
                }
                bookPrevious = book
//                MyLogger.d("MyDatabase - findPreviousParentBook previous=" + bookPrevious?.name)
            }
        }
        return null
    }

    fun findNextParentBook(bookCurrent: Book): Book? {
        if (bookCurrent == null) {
            return null
        }
        var foundCurrent = false;
        for (book in booksSelected) {
            if (bookCurrent.id.equals(book.id)) {
                foundCurrent = true
                continue
            }

            if (foundCurrent && book.level.equals(Book.LEVEL_PARENT)) {
                return book
            }
        }
        return null
    }

    fun findPreviousChildBook(bookCurrent: Book): Book? {
        var bookPrevious: Book? = null
        for (book in booksSelected) {
            if (book.level.equals(Book.LEVEL_CHILD)) {
                if (book.id.equals(bookCurrent.id)) {
                    return bookPrevious
                }
                bookPrevious = book
            }
        }
        return null
    }

    fun findNextChildBook(bookChild: Book): Book? {
        var foundCurrent = false;
        for (book in booksSelected) {
            MyLogger.d("MyDatabase - findNextChildBook book=" + book.name + " id=" + book.id + " childName=" + bookChild.name + " childId=" + bookChild.id)
            if (bookChild.id.equals(book.id)) {
                foundCurrent = true
                continue
            }

            if (foundCurrent && book.level.equals(Book.LEVEL_CHILD)) {
                return book
            }
        }
        return null
    }

    fun saveNote(note: Note): Boolean {
        MyLogger.d("MyDatabase - saveNote - note.id=" + note.id + " day=" + note.day + " value=" + note.value)
        if (note.day == null || note.value == null) {
            MyLogger.e("MyDatabase - saveNoteDb - NULL value not allowed")
            return true
        }

        var noteExisting = findNoteByDay(note.id, note.day)
        if (noteExisting == null) {
            return saveNoteDb(note)
        } else {
            return updateNoteDb(note)
        }
    }

    fun saveNoteDb(note: Note): Boolean {
        if (!createOrOpen()) {
            return false
        }
        MyLogger.d("MyDatabase - saveNoteDb - note.id=" + note.id + " day=" + note.day + " value=" + note.value)
        removeNoteInDb(note)
        try {
            db!!.execSQL(
                "insert into " + note.id +
                        "(day, value)values ('" +
                        note.day + "','" +
                        note.value + "')"
            )
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.s("MyDatabase - saveNoteDb - error:" + e)
            reportError(note.id)
            return false
        }
        return true
    }

    fun updateNoteDb(note: Note): Boolean {
        if (!createOrOpen()) {
            return false
        }
        MyLogger.d("MyDatabase - updateNoteDb - note.id=" + note.id + " day=" + note.day + " value=" + note.value)
        try {
            db!!.execSQL(
                "UPDATE " + note.id +
                        " SET value='" + note.value + "'" +
                        " WHERE day='" + note.day + "'"
            )
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.s("MyDatabase - updateNoteDb - error:" + e)
            reportError(note.id)
            return false
        }
        return true
    }

    fun getMin(book: Book): Float {
        if (book == null) {
            return 0f
        }
        var myNotes = this.readNotes(book)
        var min: Float = MAX_FLOAT
        for (note in myNotes) {
            try {
                if (note != null &&
                    note.value != null &&
                    MyConverter.toFloat(note.value) < min
                ) {
                    min = MyConverter.toFloat(note.value)
                }
            } catch (e: NumberFormatException) {
                min = 0f
            }
        }
        return min
    }

    fun getMax(book: Book): Float {
        if (book == null) {
            return 0f
        }
        var myNotes = readNotes(book)
        var max = 0f
        for (note in myNotes) {
            try {
                if (note != null &&
                    note.value != null &&
                    MyConverter.toFloat(note.value) > max
                ) {
                    max = MyConverter.toFloat(note.value)
                }
            } catch (e: NumberFormatException) {
                max = 0f
            }
        }
        return max
    }

    /////////////////////////////////////////////////////////////////////
    open fun createOrOpen(): Boolean {
//        MyLogger.d("MyDatabase - createOrOpen")
        if (db != null) {
            return true
        }
        try {
            MyLogger.d("MyDatabase - createOrOpen")
//            myApplication = MyApplication.instance
//
//            if (MyCommon.EXT_STORAGE) {
//                databasePath =
//                    Environment.getExternalStoragePublicDirectory(MyCommon.DB_DIR).toString()
//            } else {
//                databasePath = myApplication!!.getDatabasePath(MyCommon.DB_NAME).absolutePath
//            }
            MyLogger.d("databasePath=" + databasePath)
            db = myApplication!!.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
        } catch (e: Exception) {
            MyLogger.d("Error creating or opening database:$e")
            return false
        }
        MyLogger.d("MyDatabase - createOrOpen - OK")
        return true
    }

    /**
     * Delete database file
     */
    fun delete() {
        try {
            myApplication!!.deleteDatabase(databasePath)
        } catch (e: SQLiteException) {
            MyLogger.d("Error to delete database: $e")
        }
    }

    val TABLE_BOOKS_CREATE: String = "CREATE TABLE " + TABLE_BOOKS +
            "(\n" +
//            "\tn INTEGER PRIMARY KEY  AUTOINCREMENT,\n" +
            "\tid TEXT,\n" +
            "\tlevel INTEGER,\n" +
            "\tname TEXT,\n" +
            "\tparent TEXT,\n" +
            "\ticon TEXT,\n" +
            "\ttype INTEGER,\n" +
            "\tmin FLOAT,\n" +
            "\tmax FLOAT\n" +
            ");\n"

    val TABLE_NOTE_CREATE: String =
        "(\n" +
//            "\tn INTEGER PRIMARY KEY  AUTOINCREMENT,\n" +
                "\tday INTEGER,\n" +
                "\tvalue TEXT\n" +
                ");\n"

    fun createTable(table: String, command: String): Boolean {
        MyLogger.d("MyDatabase - createTable - table=" + table)
        if (!createOrOpen()) {
            MyLogger.e("MyDatabase - createTable - error opening database")
            return false
        }
        try {
            db!!.execSQL(command)
            MyLogger.d("MyDatabase - createTable - OK")
        } catch (e: Exception) {
//            MyLogger.e("MyDatabase - createTable - error: $e")
            return false
        }
        return true
    }

    fun createTableBooks(): Boolean {
        return createTable(TABLE_BOOKS, TABLE_BOOKS_CREATE)
    }

    fun createTableNote(id: String): Boolean {
        MyLogger.d("MyDatabase - createTableNote table=" + id)
        return createTable(
//            TABLE_NOTE +
            id,
            "CREATE TABLE " + id + TABLE_NOTE_CREATE
        )
    }

    fun addBook(bookNew: Book): Boolean {
        MyLogger.d("MyDatabase - addBook book=" + bookNew.name + " next id=" + myApplication?.bookId + " isMain=" + isMain)
        debugShowBooks()
        MyLogger.d("MyDatabase - addBook - bookNew=" + bookNew.name + " id=" + bookNew.id + " parent=" + bookNew.parentId + " level=" + bookNew.level)
        if (bookNew.id == null || bookNew.name == null || bookNew.parentId == null || bookNew.min == null || bookNew.max == null || bookNew.icon == null) {
            MyLogger.e("MyDatabase - addBookDb NULL not allowed")
            return false
        }

        var bookFound: Book? = null
        for (book in books) {
//            MyLogger.d("MyDatabase - addBook - loop  bookNew=" + bookNew.id + " book=" + book.id + " name=" +  book.name)
            if (book.id.equals(bookNew.id)) {
//                MyLogger.d("MyDatabase - addBook - loop FOUND book=" + book.id + " bookNew=" + bookNew.id)
                bookFound = book
                break
            }
        }
        MyLogger.d("MyDatabase - addBook - bookFound=" + bookFound)

        if (bookFound == null) {
            MyLogger.d("MyDatabase - addBook - NEW")
            books.add(bookNew)
            addBookDb(bookNew)
            if (bookNew.level == Book.LEVEL_CHILD) {
                createTableNote(bookNew.id)
            }
        } else {
            bookFound.name = bookNew.name
            bookFound.type = bookNew.type
            bookFound.min = bookNew.min
            bookFound.max = bookNew.max
            bookFound.icon = bookNew.icon
            updateBookDb(bookNew)
        }
        debugShowBooks()
        return true
    }

    fun addBookDb(book: Book): Boolean {
        MyLogger.d("MyDatabase - addBookDb - book=" + book.name + " id=" + book.id + " parent=" + book.parentId)
        if (!createOrOpen()) {
            return false
        }
        MyLogger.d("MyDatabase - addBookDb - add to db id=" + book.id + " name=" + book.name)
        try {
            db!!.execSQL(
                "insert into " + TABLE_BOOKS +
                        "(id, level, name, parent, icon, type, min, max)values ('" +
                        book.id + "','" +
                        book.level + "','" +
                        book.name + "','" +
                        book.parentId + "','" +
                        book.icon + "','" +
                        book.type + "','" +
                        book.min + "','" +
                        book.max +
                        "')"
            )
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.s("MyDatabase - addOrUpdateBook - error:" + e)
            reportError(TABLE_BOOKS)
            return false
        }
        if (book.level == Book.LEVEL_CHILD) {
            createTableNote(book.id)
        }
        return true
    }

    /**
     * UPDATE employees
    SET city = 'Toronto',
    state = 'ON',
    postalcode = 'M5P 2N7'
    WHERE
    employeeid = 4;
     */
    fun updateBookDb(book: Book): Boolean {
        MyLogger.d("MyDatabase - updateBookDb - book=" + book.name + " id=" + book.id + " parent=" + book.parentId)
        if (!createOrOpen()) {
            return false
        }
        MyLogger.d("MyDatabase - updateBookDb - add to db id=" + book.id + " name=" + book.name)
        try {
            db!!.execSQL(
                "UPDATE " + TABLE_BOOKS + " SET " +
                        "name='" + book.name + "'," +
                        "icon='" + book.icon + "'," +
                        "type='" + book.type + "'," +
                        "min='" + book.min + "'," +
                        "max='" + book.max + "'" +
                        " WHERE id='" + book.id + "'"
            )
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.s("MyDatabase - addOrUpdateBook - error:" + e)
            reportError(TABLE_BOOKS)
            return false
        }
        if (book.level == Book.LEVEL_CHILD) {
            createTableNote(book.id)
        }
        return true
    }

    fun removeBookInDb(book: Book): Boolean {
        MyLogger.d("MyDatabase - removeBookInDb book=" + book.name + " id=" + book.id)
        var table: String = TABLE_BOOKS
        var column: String = "id"
//        MyLogger.d("MyDatabase - removeBookInDb in $table $column=$value")
        if (!createOrOpen()) {
            MyLogger.e("MyDatabase - removeBookInDb error opening database")
            return false
        }
        try {
            db!!.execSQL("DELETE FROM $table WHERE $column='${book.id}'")
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.e("Error deleting from $table column=$column value=${book.id} Error:$e")
            return false
        }
        return true
    }

    fun removeNoteInDb(note: Note): Boolean {
        MyLogger.d("MyDatabase - removeNoteInDb book=" + findBookById(note.id) + " day=" + note.day)
        var table: String = note.id
        var column: String = "day"
        var value: Int = note.day
//        MyLogger.d("MyDatabase - deleteNoteNoLog in $table $column=$value")
        if (!createOrOpen()) {
            MyLogger.e("MyDatabase - deleteNoteNoLog - error opening database")
            return false
        }
        try {
            db!!.execSQL("DELETE FROM $table WHERE $column='$value'")
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.e("MyDatabase - deleteNoteNoLog - error deleting table=$table column=$column value=$value Error:$e")
            return false
        }
        return true
    }

    fun readBooks(): MutableList<Book>? {
        MyLogger.d("MyDatabase - readBooks")
        myApplication?.bookId = 1
        if (!createOrOpen()) {
            return null
        }
        myApplication?.checkId = 0
        val books: MutableList<Book> = ArrayList<Book>()
        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery("SELECT * FROM $TABLE_BOOKS", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex("id"))
                    val level = cursor.getInt(cursor.getColumnIndex("level"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val parent = cursor.getString(cursor.getColumnIndex("parent"))
                    val icon = cursor.getString(cursor.getColumnIndex("icon"))
                    val type = cursor.getInt(cursor.getColumnIndex("type"))
                    val min = cursor.getFloat(cursor.getColumnIndex("min"))
                    val max = cursor.getFloat(cursor.getColumnIndex("max"))
//                    MyLogger.d("MyDatabase - readBooks name=" + name + " parent=" + parent)

                    if (id.length >= 1) {  // to avoid crash in substring
                        var idInt = MyConverter.toInt(id.substring(1))
                        if (idInt >= myApplication?.bookId!!) {
                            myApplication?.bookId = idInt + 1
                        }
                    }
                    MyLogger.d("MyDatabase - readBooks name=" + name + " id=" + id + " myApplication?.bookId=" + myApplication?.bookId + " icon=" + icon)

                    if (!icon.isEmpty()) {
                        var checkNum = MyConverter.toInt(icon)
                        if (checkNum >= MyCommon.COLORS.size) {
                            checkNum -= MyCommon.COLORS.size
                        }
                        myApplication?.checkId =
                            Math.max(myApplication?.checkId!!, checkNum)
                    }
                    MyLogger.d("MyDatabase - readBooks name=" + name + " id=" + id + " myApplication?.bookId=" + myApplication?.bookId + " icon=" + icon + " checkId=" + myApplication?.checkId)

                    books.add(
                        Book(
                            id, level, name, parent, icon,
                            type, min, max
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: SQLiteException) {
            MyLogger.s("MyDatabase - readBooks error:" + e)
            reportError(TABLE_BOOKS)
        }
        cursor?.close()
        MyLogger.d("MyDatabase - readBooks size=" + books.size)
//        MyUtils.showAll(books)
        return books
    }

    fun readNotes(book: Book): MutableList<Note> {
        MyLogger.d("MyDatabase - readNotes book=" + book.name + " id=" + book.id)
        val notes: MutableList<Note> = ArrayList<Note>()
        if (!createOrOpen()) {
            return notes
        }
        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery(
                "SELECT * FROM ${book.id}" +
                        " ORDER BY day DESC", null
            )
            if (cursor.moveToFirst()) {
                do {
                    val day = cursor.getInt(cursor.getColumnIndex("day"))
                    val value = cursor.getString(cursor.getColumnIndex("value"))
                    var note = Note(book.id, day, value)
                    notes.add(note)
                } while (cursor.moveToNext())
            }
        } catch (e: SQLiteException) {
            MyLogger.s("MyDatabase - readNotes error:" + e)
            reportError(book.id)
        }
        cursor?.close()
        MyLogger.d("MyDatabase - readNotes size=" + notes.size)
        return notes
    }

    fun readFirstNote(book: Book): Note? {
        MyLogger.d("MyDatabase - readFirstNote book=" + book.name + " id=" + book.id)
        var note: Note? = null
        if (!createOrOpen()) {
            return note
        }
        var cursor: Cursor? = null
        try {
            cursor = db!!.rawQuery(
                "SELECT * FROM ${book.id}" +
                        " ORDER BY day ASC", null
            )
            if (cursor.moveToFirst()) {
                val day = cursor.getInt(cursor.getColumnIndex("day"))
                val value = cursor.getString(cursor.getColumnIndex("value"))
                note = Note(book.id, day, value)
            }
        } catch (e: SQLiteException) {
            MyLogger.s("MyDatabase - readFirstNote error:" + e)
            reportError(book.id)
        }
        cursor?.close()
        return note
    }

    fun updateAny(
        table: String,
        columnSearch: String, valueSearch: String,
        column: String, value: String
    ): Boolean {
        MyLogger.d("MyDatabase - updateRecord in $table search: $columnSearch=$valueSearch update $column=$value")
        if (!createOrOpen()) {
            return false
        }
        try {
            db!!.execSQL(
                "UPDATE " + table + " SET " + column + "='" + value + "'"
                        + " WHERE " + columnSearch + "='" + valueSearch + "'"
            )
            setChangedFlag()
        } catch (e: Exception) {
            MyLogger.s(
                ("Error updating " + table + " " +
                        column + "=" + value + ":" + e)
            )
            reportError(table)
            return false
        }
        return true
    }

    fun findNoteByDay(table: String, day: Int): Note? {
        MyLogger.d("MyDatabase - findNoteByDay table=$table + day=$day")
        if (!createOrOpen()) {
            return null
        }
        var cursor: Cursor? = null
        var note: Note? = null
        try {
            cursor = db!!.rawQuery(
                "SELECT  * FROM " + table +
                        " WHERE " + "day" + " = " + day, null
            )
            if (cursor.moveToFirst()) {
                val value = cursor.getString(cursor.getColumnIndex("value"))
                note = Note(table, day, value)
            }
            while (cursor.moveToNext());
        } catch (e: SQLiteException) {
            // not found
        }
        cursor?.close()
        return note
    }

    fun showAllNotes(book: Book) {
        MyLogger.d("--------All Notes table=${book.id} -------")
        var notesShow = this.readNotes(book)
        if (notesShow == null) {
            MyLogger.d("MyDatabase - showAllNotes - notesShow=null")
        } else {
            for (note: Note in notesShow) {
                MyLogger.d("MyDatabase - showAllNotes book=" + book.name + " id=" + book.id + " day=" + note.day + " value=" + note.value)
            }
        }
    }

    fun showSelectedBooks(books: MutableList<Book>) {
        MyLogger.d("--------All-------")
        for (book: Book in books) {
            MyLogger.d(book.level.toString() + " ${book.id} - ${book.parentId} - ${book.name} - ${book.type}")
        }
    }

    fun debugShowBooks() {
        if (!MyCommon.DEBUG_SHOW_BOOKS) {
            return
        }
        MyLogger.d("--------All-------")
        for (book: Book in books) {
            MyLogger.d(book.level.toString() + " ${book.id} - ${book.parentId} - ${book.name} - ${book.type}")
        }
    }


    fun reportError(table: String) {

    }

    fun setChangedFlag() {

    }

    fun close() {
        MyLogger.d("MyDatabase - close")
        db?.close()
    }

    fun clearTable(table: String): Boolean {
        MyLogger.d("MyDatabase - clearTable - $table" + " isMain=" + isMain)
        try {
            db!!.execSQL("delete from $table")
            setChangedFlag()
        } catch (e: SQLiteException) {
            MyLogger.s("MyDatabase - clearTable - error clearing table $table :$e")
            return false
        }
        return true
    }

    fun dropTable(table: String): Boolean {
        MyLogger.d("MyDatabase - clearTable - $table")
        try {
            db?.execSQL("DROP TABLE IF EXISTS table_Name");
            setChangedFlag()
        } catch (e: SQLiteException) {
            MyLogger.s("MyDatabase - clearTable - error clearing table $table :$e")
            return false
        }
        return true
    }

    /**
     * https://stackoverflow.com/questions/4253804/insert-new-column-into-table-in-sqlite
     */
    fun recreateBooks(){

    }


}

