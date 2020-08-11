package com.heathkev.quizado.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_settings.*

class AboutFragment : MainNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
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
