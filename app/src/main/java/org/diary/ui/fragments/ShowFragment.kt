package org.diary.ui.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import org.diary.databinding.FragmentShowBinding
import org.diary.utils.MyLogger
import org.diary.utils.MyUtils
import org.diary.viewmodel.ShowViewModel

/**
 * Fragment to enter data
 */
class ShowFragment constructor() : Fragment() {

    private lateinit var showViewModel: ShowViewModel
    private lateinit var binding: FragmentShowBinding
    private val myApplication: MyApplication = MyApplication.instance!!
    var owner: LifecycleOwner = this

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //        MyLogger.d("ShowFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show, container, false)
        showViewModel = ShowViewModel(this, binding.showImage)
        binding.lifecycleOwner = this
        binding.xmlShowViewModel = showViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyLogger.d("ShowFragment - onViewCreated")
        myApplication?.fragment = this
        myApplication?.showFragment = this

        binding.showFrameTable.visibility = View.VISIBLE
        binding.showFrameGraph.visibility = View.INVISIBLE
        binding.showFrameRect.visibility = View.INVISIBLE

        binding.showRecycler.visibility = View.VISIBLE
        binding.showImage.visibility = View.INVISIBLE
        binding.showScrollImage.visibility = View.INVISIBLE

        val bookParentObserver = Observer<String> { newValue ->
            binding.showParentText.text = newValue
        }
        showViewModel.liveParentName.observe(owner, bookParentObserver)

        val bookNameObserver = Observer<String> { newValue ->
            binding.showTitleText.text = newValue
        }
        showViewModel.liveBookName.observe(owner, bookNameObserver)

        val imageObserver = Observer<Bitmap> { newValue ->
            binding.showImage.background = BitmapDrawable(getResources(), newValue)
        }
        showViewModel.liveImage.observe(owner, imageObserver)

        val stepObserver = Observer<Int> { newValue ->
            binding.showStepText.text = MyApplication.instance?.getText("step") +
                    ": " + newValue.toString()
        }
        showViewModel.liveStep.observe(owner, stepObserver)

        val genreObserver = Observer<Int> { newValue ->
            binding.showGenreText.text = MyApplication.instance
                ?.getText(MyCommon.GENRES[newValue])
        }
        showViewModel.liveGenre.observe(owner, genreObserver)

        val showTypeObserver = Observer<Int> { newValue ->
            if (newValue == MyCommon.TYPE_TABLE) {
                binding.showFrameTable.visibility = View.VISIBLE
                binding.showFrameGraph.visibility = View.INVISIBLE
                binding.showFrameRect.visibility = View.INVISIBLE
                binding.showRecycler.visibility = View.VISIBLE
                binding.showImage.visibility = View.INVISIBLE
                binding.showScrollImage.visibility = View.INVISIBLE
            }
            if (newValue == MyCommon.TYPE_GRAPH) {
                binding.showFrameTable.visibility = View.INVISIBLE
                binding.showFrameGraph.visibility = View.VISIBLE
                binding.showFrameRect.visibility = View.INVISIBLE
                binding.showRecycler.visibility = View.INVISIBLE
                binding.showImage.visibility = View.VISIBLE
                binding.showScrollImage.visibility = View.VISIBLE
            }
            if (newValue == MyCommon.TYPE_RECT) {
                binding.showFrameTable.visibility = View.INVISIBLE
                binding.showFrameGraph.visibility = View.INVISIBLE
                binding.showFrameRect.visibility = View.VISIBLE
                binding.showRecycler.visibility = View.INVISIBLE
                binding.showImage.visibility = View.VISIBLE
                binding.showScrollImage.visibility = View.VISIBLE
//                if (MyCommon.DEBUG_COPY_DB) {
//                    MyUtils.copyDb()
//                }
            }
        }
        showViewModel.liveShowType.observe(owner, showTypeObserver)

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        } else {
//            MyUtils.hideAnyKeyboard()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        MyLogger.d("ShowFragment - onResume")
        MyApplication.instance?.fragment = this
        MyApplication.instance?.needKeyboard = false
//        MyUtils.hideAnyKeyboard()
    }

}