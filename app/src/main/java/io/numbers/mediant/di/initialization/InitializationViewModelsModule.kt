package io.numbers.mediant.di.initialization

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.initialization.InitializationViewModel

@Module
abstract class InitializationViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(InitializationViewModel::class)
    abstract fun bindInitializationViewModel(viewModel: InitializationViewModel): ViewModel
}