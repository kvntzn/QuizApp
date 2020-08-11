package com.heathkev.quizado.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment

class DispatchInsetsNavHostFragment : NavHostFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnApplyWindowInsetsListener { v, insets ->
            // During fragment transitions, multiple fragment's view hierarchies can be added at the
            // same time. If one consumes window insets, the other might not be layed out properly.
            // To workaround that, make sure we dispatch the insets to all children, regardless of
            // how they are consumed.
            (v as? ViewGroup)?.forEach { child ->
                child.dispatchApplyWindowInsets(insets)
            }
            insets
        }
    }
}