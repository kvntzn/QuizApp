package com.heathkev.quizado.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.heathkev.quizado.ui.DARK_MODE
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : MainNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
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

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        val isDarkMode = sharedPref.getBoolean(DARK_MODE, false)
        settings_switch.isChecked = isDarkMode

        settings_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            with(sharedPref.edit()){
                putBoolean(DARK_MODE, isChecked)
                apply()
            }
        }
    }
}
