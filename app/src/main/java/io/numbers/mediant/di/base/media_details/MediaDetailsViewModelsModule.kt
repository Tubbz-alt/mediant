package io.numbers.mediant.di.base.media_details

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.media_details.MediaDetailsViewModel


@Suppress("UNUSED")
@Module
abstract class MediaDetailsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MediaDetailsViewModel::class)
    abstract fun bindMediaDetailsViewModel(viewModel: MediaDetailsViewModel): ViewModel
}