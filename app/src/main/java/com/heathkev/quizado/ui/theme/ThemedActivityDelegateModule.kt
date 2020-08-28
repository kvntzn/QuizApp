package com.heathkev.quizado.ui.theme

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
@Suppress("UNUSED")
abstract class ThemedActivityDelegateModule {

    @Singleton
    @Binds
    abstract fun provideThemedActivityDelegate(
        impl: ThemedActivityDelegateImpl
    ): ThemedActivityDelegate
}