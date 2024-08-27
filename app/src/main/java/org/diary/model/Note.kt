package org.diary.model

class Note constructor(
//    var book: Book,
    var id: String,
    var day: Int,
    var value: String,
) {

    init{

    }
    companion object{
        val TYPE_STRING = 1
        val TYPE_INT = 2
        val TYPE_FLOAT = 3
    }
}