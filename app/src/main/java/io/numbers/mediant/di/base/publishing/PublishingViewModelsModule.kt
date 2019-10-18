package io.numbers.mediant.di.base.publishing

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.publishing.PublishingViewModel

@Suppress("UNUSED")
@Module
abstract class PublishingViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PublishingViewModel::class)
    abstract fun bindPublishingViewModel(viewModel: PublishingViewModel): ViewModel
}