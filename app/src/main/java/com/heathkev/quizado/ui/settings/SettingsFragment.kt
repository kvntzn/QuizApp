package com.heathkev.quizado.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.heathkev.quizado.R
import com.heathkev.quizado.model.Theme
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*

@AndroidEntryPoint
class SettingsFragment : MainNavigationFragment() {

    private val viewModel: SettingsViewModel by viewModels()

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

        settings_switch.isChecked = viewModel.currentTheme == Theme.DARK

        settings_switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.setTheme(Theme.DARK)
            }else{
                viewModel.setTheme(Theme.LIGHT)
            }

        }
    }

    //TODO Multiple themes
    private fun getTitleForTheme(theme: Theme) = when (theme) {
        Theme.LIGHT -> getString(R.string.settings_theme_light)
        Theme.DARK -> getString(R.string.settings_theme_dark)
        Theme.SYSTEM -> getString(R.string.settings_theme_system)
        Theme.BATTERY_SAVER -> getString(R.string.settings_theme_battery)
    }
}
