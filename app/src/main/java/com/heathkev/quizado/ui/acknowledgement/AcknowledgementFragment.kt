package com.heathkev.quizado.ui.acknowledgement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.heathkev.quizado.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_settings.*


class AcknowledgementFragment : MainNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acknowledgement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnApplyWindowInsets { _, insets, _ ->
            status_bar.run {
                layoutParams.height = insets.systemWindowInsetTop
                isVisible = layoutParams.height > 0
                requestLayout()
            }
        }
    }
}