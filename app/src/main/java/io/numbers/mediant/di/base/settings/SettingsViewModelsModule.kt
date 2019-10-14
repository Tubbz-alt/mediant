package io.numbers.mediant.di.base.settings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.settings.SettingsViewModel
import io.numbers.mediant.ui.settings.textile.TextileSettingsViewModel

@Suppress("UNUSED")
@Module
abstract class SettingsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TextileSettingsViewModel::class)
    abstract fun bindTextileSettingsViewModel(viewModel: TextileSettingsViewModel): ViewModel
}