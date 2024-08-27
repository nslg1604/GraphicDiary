package org.diary.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.databinding.FragmentChildBinding
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.ChildViewModel

/**
 * Fragment to enter data
 */
class ChildFragment : Fragment() {
    private lateinit var childViewModel: ChildViewModel
    private lateinit var binding: FragmentChildBinding
    var owner: LifecycleOwner = this
    var myApplication = MyApplication.instance

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        MyLogger.d("ChildFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_child, container, false)
        MyApplication.instance?.editText = binding.childEdit
        MyApplication.instance?.editTextMin = binding.childEditMin
        MyApplication.instance?.editTextMax = binding.childEditMax
        childViewModel = ChildViewModel(this)
        binding.lifecycleOwner = this
        binding.xmlChildViewModel = childViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyLogger.d("ChildFragment - onViewCreated")
        MyUtils.showKeyboard(binding.childEdit)

        // Create the observer which updates the UI.
        val liveTypeObserver = Observer<Int> { newValue ->
            setTypeColors(newValue)
        }
        childViewModel.liveType.observe(owner, liveTypeObserver)

        val liveEditObserver = Observer<String> { newValue ->
            binding.childEdit.setText(newValue)
        }
        childViewModel.liveEdit.observe(owner, liveEditObserver)

        setTypeColors(MyCommon.VALUE_TYPE_INTEGER)
    }

    fun setTypeColors(newValue: Int){
//        MyLogger.d("ChildFragment - setTypeColors newValue=" + newValue)
        when (newValue){
            MyCommon.VALUE_TYPE_INTEGER -> {
                binding.childTypeText.text = myApplication?.getText("integer")
                binding.childLayoutMinMax.visibility = View.VISIBLE
            }
            MyCommon.VALUE_TYPE_FLOAT -> {
                binding.childTypeText.text = myApplication?.getText("float")
                binding.childLayoutMinMax.visibility = View.VISIBLE
            }
            MyCommon.VALUE_TYPE_TEXT -> {
                binding.childTypeText.text = myApplication?.getText("text")
                binding.childLayoutMinMax.visibility = View.GONE
            }
        }
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//
//        } else {
//            MyUtils.hideKeyboard(binding.childEdit)
//            MyUtils.hideKeyboard(binding.childMin)
//            MyUtils.hideKeyboard(binding.childMax)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        MyUtils.hideKeyboard(binding.childEdit)
//        MyUtils.hideKeyboard(binding.childEditMin)
//        MyUtils.hideKeyboard(binding.childEditMax)
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        MyApplication.instance?.fragment = this
        MyApplication.instance?.needKeyboard = true
        //       MyLogger.d("HomeFragment - onResume")
    }

}