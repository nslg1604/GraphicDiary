package org.diary.utils

import kotlin.math.sign

object MyConverter {

    fun toFloat(str: String): Float{
        if (str == null || str.isEmpty()){
            return 0f
        }
        try {
            var strMy = str.trim().replace("+", "")
            if (strMy.substring(0, 1).equals("-")){
                return -(strMy.replace("-", "").toFloat())
            }
            return str.toFloat()
        }
        catch (e: NumberFormatException){
            return 0f
        }
    }

    fun toInt(str: String): Int{
        if (str == null){
            return 0
        }
        try {
            return str.toInt()
        }
        catch (e: NumberFormatException){
            return 0
        }
    }
}