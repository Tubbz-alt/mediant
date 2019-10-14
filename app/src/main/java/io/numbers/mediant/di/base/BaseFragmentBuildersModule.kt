package io.numbers.mediant.di.base

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.ui.initialization.InitializationFragment
import io.numbers.mediant.ui.main.MainFragment
import io.numbers.mediant.ui.settings.SettingsFragment
import io.numbers.mediant.ui.settings.textile.TextileSettingsFragment

// Provides all fragments extending from DaggerFragment in BaseActivity scope as Dagger client.

@Suppress("UNUSED")
@Module
abstract class BaseFragmentBuildersModule {

    // 1. Let Dagger know InitializationFragment is a potential client. Therefore, we do NOT need to
    //    write `AndroidInjection.inject(this)` in InitializationFragment.onCreate() method.
    // 2. Dagger will generate InitializationFragmentSubcomponent under the hook with the following
    //    method.
    @ContributesAndroidInjector
    abstract fun contributeInitializationFragment(): InitializationFragment

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeTextileSettingsFragment(): TextileSettingsFragment

    // Add new Fragments as Dagger client here. Dagger will automatically generate
    // XXXFragmentSubcomponents.
}