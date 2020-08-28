package com.heathkev.quizado.domain.settings

import android.os.Build
import com.heathkev.quizado.data.prefs.PreferenceStorage
import com.heathkev.quizado.di.DefaultDispatcher
import com.heathkev.quizado.model.Theme
import com.heathkev.quizado.model.themeFromStorageKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.heathkev.quizado.result.Result
import kotlinx.coroutines.flow.map


open class ObserveThemeModeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Theme>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Theme>> {
        return preferenceStorage.observableSelectedTheme.map {
            val theme = themeFromStorageKey(it)
                ?: when {
                    Build.VERSION.SDK_INT >= 29 -> Theme.SYSTEM
                    else -> Theme.BATTERY_SAVER
                }
            Result.Success(theme)
        }
    }
}
