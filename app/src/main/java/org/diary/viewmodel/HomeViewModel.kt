package org.diary.viewmodel

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.model.MyDatabase
import org.diary.ui.adapters.HomeAdapter
import org.diary.ui.fragments.HomeFragment
import org.diary.ui.fragments.ValueFragment
import org.diary.utils.MyFileUtils
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import kotlin.coroutines.CoroutineContext

class HomeViewModel constructor(
    homeFragment: HomeFragment, coroutineContext: CoroutineContext
) :
    ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = coroutineContext

    // Create a LiveData
    val currentVisibility: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val myApplication: MyApplication = MyApplication.instance!!
    var homeFragment: HomeFragment = homeFragment
    var homeAdapter: HomeAdapter ?= null

    init {
        MyLogger.d("HomeViewModel - init")

        var books: MutableList<Book> =
            MyApplication.instance?.myDatabase?.prepareAll()!!
        if (books.size >= MyCommon.SIZE_ADD_EMPTY){
            books.add(Book("", 0, "", "", "", 0, 0f, 0f))
        }
        homeAdapter = HomeAdapter(books)
        myApplication?.homeAdapter = homeAdapter
        myApplication?.homeViewModel = this


//        launch {
//            val myProgress = async(Dispatchers.IO) { showProgress() }
//            cancelProgress(myProgress.await())
//        }
    }

//    fun showProgress() {
//        MyLogger.d("HomeViewModel - showProgress")
//        MyUtils.sleep(5000)
//        MyLogger.d("HomeViewModel - showProgress - end")
//    }
//
//    fun cancelProgress(async: Unit) {
//        MyLogger.d("HomeViewModel - cancelProgress")
//        currentVisibility.value = View.INVISIBLE
//    }

    fun addTitle(): String {
        return myApplication.getText("categories")
    }

    fun addTextSchema(): String {
        return myApplication.getText("page_schema")
    }

    fun addTextFill(): String {
        return myApplication.getText("page_fill")
    }

    fun addTextShow(): String {
        return myApplication.getText("page_show")
    }

    fun addCategory() {
        MyLogger.d("HomeViewModel - addCategory")
//        testCoroutine()

        myApplication?.bookCurrent = null // means new category
        MyUtils.changeFragment(homeFragment, ValueFragment(), null)
    }

    fun testCoroutine() {
        MyLogger.d("Start")

        // Start a coroutine
        GlobalScope.launch {
            delay(1000)
            MyLogger.d("Hello")
        }

        Thread.sleep(2000) // wait for 2 seconds
        MyLogger.d("Stop1")
        runBlocking {
            delay(2000)
            test2(5)
        }
        MyLogger.d("Stop2")
    }

    suspend fun test2(n: Int): Int {
        MyLogger.d("test2")
        delay(5000)
        MyLogger.d("test2 - end")
        return n
    }

    fun clickContinue() {

    }
}
