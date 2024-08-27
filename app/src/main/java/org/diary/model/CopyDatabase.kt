package org.diary.model

import android.os.Environment
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.utils.MyFileUtils
import org.diary.utils.MyLogger
import java.io.File

/**
 *
 * ATTACH DATABASE 'other.db' AS other;
 * INSERT INTO other.tbl
 * SELECT * FROM main.tbl;
 * DETACH other;
 */
class CopyDatabase constructor() {
    private var myApplication = MyApplication.instance

//    fun createCopyDb(){
//        var dirPath = Environment.getExternalStoragePublicDirectory(MyCommon.DB_DIR).toString()
//        var dir: File = File(dirPath)
//        if (!dir.exists()) {
//            MyFileUtils.createDirectory(
//                Environment.getExternalStoragePublicDirectory(MyCommon.DB_DIR).toString()
//            )
//        }
//        myApplication?.myDatabaseCopy = MyDatabase(false)
//    }

    fun myCopy() {
        MyLogger.d("CopyDatabase - myCopy")
        removeData(myApplication?.myDatabaseCopy!!)
        copyData(
            myApplication?.myDatabase!!,
            myApplication?.myDatabaseCopy!!
        )
    }

    fun myRestore() {
        MyLogger.d("CopyDatabase - myRestore")
        if (myApplication?.myDatabaseCopy!! != null) {
            removeData(myApplication?.myDatabase!!)
            copyData(
                myApplication?.myDatabaseCopy!!,
                myApplication?.myDatabase!!
            )
        }
    }

    fun removeData(db: MyDatabase) {
        MyLogger.d("CopyDatabase - removeData")
        if (db != null) {
            var books = db?.prepareAll()!!
            for (book in books) {
                MyLogger.d("CopyDatabase - removeData remove book=" + book.name)
                if (book.level == Book.LEVEL_CHILD) {
                    for (bookLow in books) {
                        if (bookLow.parentId.equals(book.id)) {
                            db?.clearTable(bookLow.id)
                        }
                    }
                }
            }
            db?.clearTable(MyDatabase.TABLE_BOOKS)
        }
    }

    fun copyData(dbSrc: MyDatabase, dbDst: MyDatabase) {
        MyLogger.d("CopyDatabase - copyData")
        var booksSrc: MutableList<Book> = dbSrc?.prepareAll()!!
        dbDst?.createTableBooks()

        for (book in booksSrc) {
            MyLogger.d("------CopyDatabase - copyData process book=" + book.name)
            dbDst?.addBook(book)
            if (book.level == Book.LEVEL_CHILD) {
                var notesSrc = dbSrc?.readNotes(book)!!
                dbDst?.createTableNote(book.id)
                for (note in notesSrc) {
                    dbDst?.saveNoteDb(note)
                }
            }
        }
    }
}