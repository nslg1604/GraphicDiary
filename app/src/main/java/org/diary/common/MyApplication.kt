package org.diary.common

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.diary.model.Book
import org.diary.ui.activities.MainActivity
import org.diary.ui.adapters.HomeAdapter
import org.diary.ui.fragments.HomeFragment
import org.diary.model.MyDatabase
import org.diary.model.Note
import org.diary.threads.ScreenThread
import org.diary.ui.adapters.ShowAdapter
import org.diary.ui.fragments.ShowFragment
import org.diary.utils.MyFileUtils
import org.diary.utils.MyLogger
import org.diary.utils.MyPrefs
import org.diary.viewmodel.ActivityMainViewModel
import org.diary.viewmodel.BookViewModel
import org.diary.viewmodel.HomeViewModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MyApplication() : Application() {

    var bookId = 1
    var activity: AppCompatActivity = MainActivity()
    var fragment: Fragment ?= null
    var bookParent: Book?= null
    var bookCurrent: Book?= null
    var bookChild: Book?= null

    var homeFragment: HomeFragment ?= null
    var homeAdapter: HomeAdapter ?= null
    var showFragment: ShowFragment?= null
    var showAdapter: ShowAdapter?= null
    var myDatabase: MyDatabase ?= null
    var myDatabaseCopy: MyDatabase ?= null
    var activityMainViewModel: ActivityMainViewModel ?= null
    var bookViewModel: BookViewModel ?= null
    var homeViewModel: HomeViewModel ?= null
    var screenThread: ScreenThread ?= null
    var finishNow = false
    var lang: String = ""
    var editText: EditText?= null
    var editTextMin: EditText?= null
    var editTextMax: EditText?= null
    var page = 0
    var needKeyboard = false
    var checkId = 0
    var screenWidth = 0
//    var orderMode = false
    var bookToChangeOrder: Book ?= null

    // Private
    private var myUncaughtHandler: Thread.UncaughtExceptionHandler? = null
    private var jsonObjectTranslations: JSONObject = JSONObject()
    var notes: MutableList<Note> ?= null


    // Main app methods
    override fun onCreate() {
        MyLogger.d("================================================")
        MyLogger.d("MyApplication - onCreate")
        super.onCreate()
        instance = this
//        myDatabase.createTableBooks()
//        myDatabase.createTestData()
//        myUncaughtHandler =
//            Thread.UncaughtExceptionHandler { thread, e -> handleUncaughtException(thread, e) }
    }

    /*
    * Methods
    */
    fun handleUncaughtException(thread: Thread?, e: Throwable?) {
        MyLogger.e("MyApplication - handleUncaughtException")
        MyLogger.e("MyApplication - handleUncaughtException - activity=$activity")
        Log.getStackTraceString(e)
        if (activity != null) {
            MyLogger.e("MyApplication - handleUncaughtException - activity name=" + activity!!.javaClass.simpleName)
        }
        val intent: Intent
        intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        System.exit(1) // kill off the crashed app
    }

    fun nextBookId(): String{
        return MyCommon.TABLE_NOTE + bookId++.toString()
    }

    companion object {
        var instance: MyApplication? = null
    }

//    @Volatile
//    var activity: AppCompatActivity? = null
//
//    fun setActivity(activity: AppCompatActivity?) {
//        this.activity = activity
//    }
//
//    fun getActivity(){
//        activity
//    }

    fun initLanguage(langNew: String?): Boolean {
        MyLogger.d("MyApplication - initLanguage - original langNew=$langNew")
        if (lang.equals(langNew)){
            MyLogger.d("MyApplication - initLanguage - already installed langNew=$langNew")
            return true
        }
        var langNew = langNew
        if (langNew == null || langNew.isEmpty()) {
            return false
        }
        var translations: String? = null
        var fileExtension: String = langNew
        val fileName = "strings/$fileExtension.json"

        try {
            translations = MyFileUtils.readTextFromAssets(this, fileName)
            if (translations == null || translations.isEmpty()) {
                return false
            }
        } catch (e: IOException) {
            MyLogger.e("MyApplication - no translation file in assets:$fileName")
            false
        }

        // Save current language to shared prefs
        // Init language
        var jsonObjectTranslations: JSONObject? = null
        jsonObjectTranslations = try {
            JSONObject(translations)
        } catch (e: JSONException) {
            return false
        }

        // language not defined
        if (jsonObjectTranslations == null) {
            return false
        }
        MyLogger.d("MyApplication - initLanguage - finally langNew=$langNew")

//        jsonObjectLang = jsonObjectTranslations
        this.jsonObjectTranslations = jsonObjectTranslations
        MyLogger.d("MyApplication - initLanguage - language installed=$langNew")

        // Save current language to shared prefs
        lang = langNew
        MyLogger.d("MyApplication -  initLanguage - activity=" + activity)
        MyPrefs.saveCurrentLanguage(activity, lang)
        return true
    }


    /**
     * Get translation
     */
    fun getText(key: String): String {
        var str: String = ""
        if (jsonObjectTranslations == null) {
            return key
        }
        if (key.isEmpty()) {
            return "?"
        }

        str = jsonObjectTranslations.optString(key)
        if (str == null || str.isEmpty()) {
            str = key
        }
        return str
    }


}
