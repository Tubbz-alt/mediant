package io.numbers.mediant.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.di.main.MainViewModelsModule
import io.numbers.mediant.ui.MainActivity

// Provides all activities as Dagger client.

@Module
abstract class ActivityBuildersModule {

    // Let Dagger know MainActivity is a potential client. Therefore, we do NOT need to
    // write `AndroidInjection.inject(this)` in MainActivity.onCreate() method.

    // Scope MainViewModelsModule only within MainActivity instead of whole app.
    // Also, Dagger will generate InitializationActivitySubcomponent under the hook with the following method.
    @ContributesAndroidInjector(modules = [MainViewModelsModule::class])
    abstract fun contributeMainActivity(): MainActivity

    // Add new Activities as Dagger client here. Dagger will automatically generate
    // XXXActivitySubcomponents.
}