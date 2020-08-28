package com.heathkev.quizado.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heathkev.quizado.domain.settings.SetThemeUseCase
import com.heathkev.quizado.model.Theme
import com.heathkev.quizado.ui.theme.ThemedActivityDelegate
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    themedActivityDelegate: ThemedActivityDelegate,
    val setThemeUseCase: SetThemeUseCase
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }
}