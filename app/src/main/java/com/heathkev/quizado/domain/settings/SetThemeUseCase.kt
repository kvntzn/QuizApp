package com.heathkev.quizado.domain.settings

import com.heathkev.quizado.data.prefs.PreferenceStorage
import com.heathkev.quizado.di.IoDispatcher
import com.heathkev.quizado.model.Theme
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class SetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Theme, Unit>(dispatcher){
    override suspend fun execute(parameters: Theme) {
        preferenceStorage.selectedTheme = parameters.storageKey
    }
}