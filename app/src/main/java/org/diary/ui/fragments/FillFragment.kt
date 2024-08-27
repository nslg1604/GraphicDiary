package org.diary.ui.fragments

import android.graphics.drawable.Drawable
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
import org.diary.databinding.FragmentFillBinding
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.FillViewModel

/**
 * Fragment to enter data
 */
class FillFragment constructor() : Fragment() {

    private lateinit var fillViewModel: FillViewModel
    private lateinit var binding: FragmentFillBinding
    var owner: LifecycleOwner = this

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //        MyLogger.d("FillFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fill, container, false)
        MyApplication.instance?.editText = binding.fillEdit
        fillViewModel = FillViewModel(this)
        binding.lifecycleOwner = this
        binding.xmlFillViewModel = fillViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyLogger.d("FillFragment - onViewCreated")
        MyUtils.showKeyboard(binding.fillEdit)

        // Create the observer which updates the UI.
        val dateObserver = Observer<String> { newValue ->
            binding.fillDateText.text = newValue
        }
        fillViewModel.currentDate.observe(owner, dateObserver)

        val bookParentObserver = Observer<String> { newValue ->
            binding.fillParentText.text = newValue
        }
        fillViewModel.liveParentName.observe(owner, bookParentObserver)

        val bookNameObserver = Observer<String> { newValue ->
            binding.fillChildText.text = newValue
        }
        fillViewModel.liveBookName.observe(owner, bookNameObserver)

        val noteValueObserver = Observer<String> { newValue ->
            binding.fillEdit.setText(newValue)
        }
        fillViewModel.liveNoteValue.observe(owner, noteValueObserver)

        val noteColorObserver = Observer<Int> { newValue ->
//            binding.fillEdit.setBackgroundColor(newValue)
        }
        fillViewModel.liveNoteColor.observe(owner, noteColorObserver)

        val noteDrawableObserver = Observer<Drawable> { newValue ->
            binding.fillEdit.background = newValue
            //MyApplication.instance?.getDrawable(newValue)
        }
        fillViewModel.liveEditDrawable.observe(owner, noteDrawableObserver)
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//        } else {
//            MyUtils.hideKeyboard(binding.fillEdit)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        MyUtils.hideKeyboard(binding?.fillEdit)
        fillViewModel.saveNote()
    }

    override fun onPause() {
        super.onPause()
        fillViewModel.saveNote()
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        MyLogger.d("FillFragment - onResume")
        MyApplication.instance?.fragment = this
        MyApplication.instance?.needKeyboard = true
    }

}