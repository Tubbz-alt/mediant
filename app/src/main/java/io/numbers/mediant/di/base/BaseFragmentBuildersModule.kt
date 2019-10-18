package io.numbers.mediant.di.base

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.di.base.initialization.InitializationViewModelsModule
import io.numbers.mediant.di.base.main.MainModule
import io.numbers.mediant.di.base.main.MainViewModelsModule
import io.numbers.mediant.di.base.permission_rationale.PermissionRationaleViewModelsModule
import io.numbers.mediant.di.base.publishing.PublishingViewModelsModule
import io.numbers.mediant.di.base.settings.SettingsViewModelsModule
import io.numbers.mediant.di.base.textile_settings.TextileSettingsViewModelsModule
import io.numbers.mediant.di.base.thread.ThreadViewModelsModule
import io.numbers.mediant.di.base.thread_creation_dialog.ThreadCreationDialogViewModelsModule
import io.numbers.mediant.di.base.thread_list.ThreadListViewModelsModule
import io.numbers.mediant.ui.initialization.InitializationFragment
import io.numbers.mediant.ui.main.MainFragment
import io.numbers.mediant.ui.main.personal_thread.PersonalThreadFragment
import io.numbers.mediant.ui.main.publishing.PublishingFragment
import io.numbers.mediant.ui.main.thread.ThreadFragment
import io.numbers.mediant.ui.main.thread_list.ThreadListFragment
import io.numbers.mediant.ui.main.thread_list.thread_creation_dialog.ThreadCreationDialogFragment
import io.numbers.mediant.ui.permission_rationale.PermissionRationaleFragment
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
    @ContributesAndroidInjector(modules = [InitializationViewModelsModule::class])
    abstract fun contributeInitializationFragment(): InitializationFragment

    @ContributesAndroidInjector(modules = [MainViewModelsModule::class, MainModule::class])
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector(modules = [ThreadViewModelsModule::class])
    abstract fun contributeThreadFragment(): ThreadFragment

    @ContributesAndroidInjector(modules = [ThreadViewModelsModule::class])
    abstract fun contributePersonalThreadFragment(): PersonalThreadFragment

    @ContributesAndroidInjector(modules = [ThreadListViewModelsModule::class])
    abstract fun contributeThreadListFragment(): ThreadListFragment

    @ContributesAndroidInjector(modules = [SettingsViewModelsModule::class])
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector(modules = [TextileSettingsViewModelsModule::class])
    abstract fun contributeTextileSettingsFragment(): TextileSettingsFragment

    @ContributesAndroidInjector(modules = [ThreadCreationDialogViewModelsModule::class])
    abstract fun contributeThreadCreationDialogFragment(): ThreadCreationDialogFragment

    @ContributesAndroidInjector(modules = [PermissionRationaleViewModelsModule::class])
    abstract fun contributePermissionRationaleFragment(): PermissionRationaleFragment

    @ContributesAndroidInjector(modules = [PublishingViewModelsModule::class])
    abstract fun contributePublishingFragment(): PublishingFragment

    // Add new Fragments as Dagger client here. Dagger will automatically generate
    // XXXFragmentSubcomponents.
}