package org.diary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.diary.R

/**
 * Fragment to initPickerForPos different settings fragments in one fragment
 * by replacing one fragment to another
 * Created by Niaz Sattarov
 */
class RootFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_root, container, false)
        val transaction = fragmentManager?.beginTransaction()
        // When this container fragment is created, we fill it with our first
        // "real" fragment
        transaction?.replace(R.id.root_frame, TitleFragment())
        transaction?.commit()
        return view
    }

    /**
     * Method running when fragment becomes visible
     * @param isVisible
     */
    override fun setUserVisibleHint(isVisible: Boolean) {
        super.setUserVisibleHint(isVisible)
        if (isVisible) {
        }
    }

    companion object {
        private const val TAG = "RootFragment"
    }
}