package com.heathkev.quizado.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.heathkev.quizado.model.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface PreferenceStorage {
    var selectedTheme: String
    var isDarkModeEnabled: Boolean
    var observableSelectedTheme: Flow<String>
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class SharedPreferenceStorage @Inject constructor(
    @ApplicationContext context: Context
) : PreferenceStorage {

    private val selectedThemeChannel: ConflatedBroadcastChannel<String> by lazy {
        ConflatedBroadcastChannel<String>().also { channel ->
            channel.offer(selectedTheme)
        }
    }

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            DARK_MODE -> selectedThemeChannel.offer(selectedTheme)
        }
    }

    override var observableSelectedTheme: Flow<String>
        get() = selectedThemeChannel.asFlow()
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var isDarkModeEnabled by BooleanPreference(prefs, DARK_MODE, false)

    override var selectedTheme by StringPreference(
        prefs, DARK_MODE, Theme.SYSTEM.storageKey
    )

    companion object{
        const val PREFS_NAME = "quizado"
        const val DARK_MODE = "darkmode"
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}


class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.value.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}