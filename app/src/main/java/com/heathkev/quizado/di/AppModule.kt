package com.heathkev.quizado.di

import android.content.Context
import com.heathkev.quizado.data.prefs.PreferenceStorage
import com.heathkev.quizado.data.prefs.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)
}