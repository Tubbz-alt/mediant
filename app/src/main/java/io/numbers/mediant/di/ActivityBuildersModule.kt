package io.numbers.mediant.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.ui.BaseActivity

// Provides all activities as Dagger client.

@Module
abstract class ActivityBuildersModule {

    // Let Dagger know BaseActivity is a potential client. Therefore, we do NOT need to
    // write `AndroidInjection.inject(this)` in BaseActivity.onCreate() method.

    // Scope BaseViewModelsModule only within BaseActivity instead of whole app.
    // Also, Dagger will generate BaseActivitySubcomponent under the hook with the following method.
    @ContributesAndroidInjector
    abstract fun contributeBaseActivity(): BaseActivity

    // Add new Activities as Dagger client here. Dagger will automatically generate
    // XXXActivitySubcomponents.
}