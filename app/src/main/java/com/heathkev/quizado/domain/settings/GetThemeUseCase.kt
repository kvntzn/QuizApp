package com.heathkev.quizado.domain.settings

import android.os.Build
import com.heathkev.quizado.data.prefs.PreferenceStorage
import com.heathkev.quizado.di.IoDispatcher
import com.heathkev.quizado.model.Theme
import com.heathkev.quizado.model.themeFromStorageKey
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


class GetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Theme>(dispatcher) {
    override suspend fun execute(parameters: Unit): Theme {
        val selectedTheme = preferenceStorage.selectedTheme
        return themeFromStorageKey(selectedTheme)
            ?: when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Theme.SYSTEM
                else -> Theme.BATTERY_SAVER
            }
    }
}
