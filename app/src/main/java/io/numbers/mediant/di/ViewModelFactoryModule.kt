package io.numbers.mediant.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.numbers.mediant.viewmodel.ViewModelProvidersFactory

@Module
abstract class ViewModelFactoryModule {

    // The @Binds annotation achieves the same thing as the following with @Providers.
    //
    // ```
    // @Providers fun bindViewModelFactory(factory: ViewModelProvidersFactory) = factory
    // ```
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProvidersFactory): ViewModelProvider.Factory
}