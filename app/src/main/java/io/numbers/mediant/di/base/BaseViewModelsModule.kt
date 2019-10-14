package io.numbers.mediant.di.base

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.initialization.InitializationViewModel
import io.numbers.mediant.ui.main.MainViewModel
import io.numbers.mediant.ui.settings.SettingsViewModel
import io.numbers.mediant.ui.settings.textile.TextileSettingsViewModel

@Suppress("UNUSED")
@Module
abstract class BaseViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(InitializationViewModel::class)
    abstract fun bindInitializationViewModel(viewModel: InitializationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TextileSettingsViewModel::class)
    abstract fun bindTextileSettingsViewModel(viewModel: TextileSettingsViewModel): ViewModel
}