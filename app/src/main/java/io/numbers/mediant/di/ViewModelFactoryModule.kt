package io.numbers.mediant.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.numbers.mediant.viewmodel.ViewModelProviderFactory

@Module
abstract class ViewModelFactoryModule {

    // The @Binds annotation achieves the same thing as the following with @Providers.
    //
    // ```
    // @Providers fun bindViewModelFactory(factory: ViewModelProviderFactory) = factory
    // ```
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}