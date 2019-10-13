package io.numbers.mediant.di.base.settings

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.ui.settings.textile.TextileSettingsFragment

@Suppress("UNUSED")
@Module
abstract class SettingsFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeTextileSettingsFragment(): TextileSettingsFragment
}