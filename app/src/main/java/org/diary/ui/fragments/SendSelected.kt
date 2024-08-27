package org.diary.ui.fragments

import org.diary.model.Book

interface SendSelected {
    /**
     * Send selected operator info to HomePageActivty
     */
    fun elementSelectedExpand(book: Book)
//    fun elementSelectedName(myElement: MyElement)
//    fun elementSelectedAdd(myElement: MyElement)
}
