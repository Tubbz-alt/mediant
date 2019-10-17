package io.numbers.mediant.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.di.base.BaseFragmentBuildersModule
import io.numbers.mediant.di.base.BaseModule
import io.numbers.mediant.ui.BaseActivity

// Provides all activities extending from DaggerAppCompatActivity as Dagger client.

@Suppress("UNUSED")
@Module
abstract class ActivityBuildersModule {

    // 1. Let Dagger know BaseActivity is a potential client. Therefore, we do NOT need to
    //    write `AndroidInjection.inject(this)` in BaseActivity.onCreate() method.
    // 2. Scope BaseFragmentBuildersModule only within BaseActivity instead of whole app.
    // 3. Dagger will generate BaseActivitySubcomponent under the hook with the following method.
    @ContributesAndroidInjector(modules = [BaseFragmentBuildersModule::class, BaseModule::class])
    abstract fun contributeBaseActivity(): BaseActivity

    // Add new Activities as Dagger client here. Dagger will automatically generate
    // XXXActivitySubcomponents.
}