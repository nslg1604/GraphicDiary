package org.diary.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.diary.R
import org.diary.common.MyApplication
import org.diary.databinding.FragmentValueBinding
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.ValueViewModel

/**
 * Fragment to enter data
 */
class ValueFragment : Fragment() {

    private lateinit var valueViewModel: ValueViewModel
    private lateinit var binding: FragmentValueBinding
    private var owner: LifecycleOwner = this

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        MyLogger.d("ValueFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_value, container, false)
        MyApplication.instance?.editText = binding.valueEdit
        valueViewModel = ValueViewModel(this)
        binding.lifecycleOwner = this
        binding.xmlValueViewModel = valueViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyLogger.d("ValueFragment - onViewCreated")
        MyUtils.showKeyboard(binding.valueEdit)

        val liveEditObserver = Observer<String> { newValue ->
            binding.valueEdit.setText(newValue)
        }
        valueViewModel.liveEdit.observe(owner, liveEditObserver)
        valueViewModel.topLayout = binding.valueMain
    }


//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//        } else {
//            MyUtils.hideKeyboard(binding.valueEdit)
//
//        }
//    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.instance?.fragment = this
        MyApplication.instance?.needKeyboard = true

        //       MyLogger.d("HomeFragment - onResume")
    }

}