package org.diary.ui.fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
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
import org.diary.utils.MyLogger
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope

//import org.diary.databinding.FragmentEmptyBinding
//import org.diary.utils.MyLogger
//import org.diary.viewmodel.EmptyViewModel

/**
 * Fragment to enter data
 */
class EmptyFragment constructor() : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.instance?.fragment = this
        MyLogger.d("EmptyFragment - CREATED")
    }


}