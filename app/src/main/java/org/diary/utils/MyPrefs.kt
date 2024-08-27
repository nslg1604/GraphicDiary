package org.diary.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.diary.R

class MyPrefs {

    companion object{
        val PREFS_NAME: String = "PREFS_NAME_DIARY"
        val PREFS_LANGUAGE: String = "PREFS_LANGUAGE"

        fun saveCurrentLanguage(activity: AppCompatActivity, lang: String) {
            // Save current language to shared prefs
            if (lang != readFromSharedPrefs(activity, PREFS_LANGUAGE)) {
                writeToSharedPrefs(activity, PREFS_LANGUAGE, lang) // save lang in initLang
            }
        }

        fun writeToSharedPrefs(activity: AppCompatActivity, name: String?, data: String?) {
            val editor = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            editor.putString(name, data)
            editor.apply()
        }

        fun readFromSharedPrefs(activity: AppCompatActivity, name: String?): String? {
            MyLogger.d("readFromSharedPrefs activity=" + activity + " PREFS_NAME=" + PREFS_NAME)

            val prefs = activity?.getPreferences(Context.MODE_PRIVATE)
//            val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(name, null) ?: return ""
        }

    }
}