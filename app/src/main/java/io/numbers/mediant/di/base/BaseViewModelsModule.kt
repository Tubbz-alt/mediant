package io.numbers.mediant.di.base

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.initialization.InitializationViewModel
import io.numbers.mediant.ui.main.MainViewModel

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
}