package io.numbers.mediant.di.base.textile_settings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.settings.textile.TextileSettingsViewModel

@Suppress("UNUSED")
@Module
abstract class TextileSettingsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(TextileSettingsViewModel::class)
    abstract fun bindTextileSettingsViewModel(viewModel: TextileSettingsViewModel): ViewModel
}