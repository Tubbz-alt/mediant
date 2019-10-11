package io.numbers.mediant.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.di.initialization.InitializationViewModelsModule
import io.numbers.mediant.ui.initialization.InitializationActivity

// Provides all activities as Dagger client.

@Module
abstract class ActivityBuildersModule {

    // Let Dagger know InitializationActivity is a potential client. Therefore, we do NOT need to
    // write `AndroidInjection.inject(this)` in InitializationActivity.onCreate() method.

    // Scope InitializationViewModelsModule only within InitializationActivity instead of whole app.
    // Also, Dagger will generate InitializationActivitySubcomponent under the hook with the following method.
    @ContributesAndroidInjector(modules = [InitializationViewModelsModule::class])
    abstract fun contributeInitializationActivity(): InitializationActivity

    // Add new Activities as Dagger client here. Dagger will automatically generate
    // XXXActivitySubcomponents.
}