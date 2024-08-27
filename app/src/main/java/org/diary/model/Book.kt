package org.diary.model

class Book constructor(
    var id: String = "",
    var level: Int = 0,  // category/child
    var name: String = "",
    var parentId: String = "",
    var icon: String,
    var type: Int = 0,  // Integer, float
    var min: Float = 0f,
    var max: Float = 0f
) {



    companion object {
        val LEVEL_MAX = 2
        val LEVEL_PARENT = 1
//        val LEVEL_MID = 2
        val LEVEL_CHILD = 2
    }

    var expanded: Boolean = true

}
