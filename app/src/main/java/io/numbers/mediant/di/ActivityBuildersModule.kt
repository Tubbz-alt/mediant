package io.numbers.mediant.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.ui.main.InitializationActivity

// Provides all activities as Dagger client.

@Module
abstract class ActivityBuildersModule {

    // Let Dagger know InitializationActivity is a potential client. Therefore, we do NOT need to
    // write `AndroidInjection.inject(this)` in InitializationActivity.onCreate() method.
    @ContributesAndroidInjector
    abstract fun contributeInitializationActivity(): InitializationActivity

    // Add new Activities as Dagger client here.
}