package org.diary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.diary.R
import org.diary.common.MyApplication
import org.diary.databinding.FragmentHomeBinding
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.HomeViewModel
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomeFragment : Fragment(), CoroutineScope {
    // for coroutine
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private var myApplication = MyApplication.instance
    var owner: LifecycleOwner = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job() // create the Job for coroutines
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        MyLogger.d("HomeFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeViewModel = HomeViewModel(this, coroutineContext)
        binding.lifecycleOwner = this
        binding.myHomeViewModel = homeViewModel

        MyLogger.d("HomeFragment - onCreateView")

        // Create the observer which updates the UI.
        val visibilityObserver = Observer<Int> { newValue ->
            // Update the UI, in this case, a TextView.
//            binding.testProgress.visibility = newValue
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        homeViewModel.currentVisibility.observe(owner, visibilityObserver)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyLogger.d("HomeFragment - onViewCreated ")

        myApplication = MyApplication.instance
        myApplication?.fragment = this
        myApplication?.homeFragment = this
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
//            MyUtils.hideAnyKeyboard()
        } else {
        }
    }

    override fun onDestroy() {
        job.cancel() // cancel the Job
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        MyLogger.d("HomeFragment - onResume")
//        MyApplication.instance?.activity?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        MyUtils.hideAnyKeyboard()
        myApplication?.fragment = this
        MyApplication.instance?.needKeyboard = false
    }


}