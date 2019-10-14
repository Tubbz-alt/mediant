package io.numbers.mediant.di.base.thread

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread.ThreadViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadViewModel::class)
    abstract fun bindThreadViewModel(viewModel: ThreadViewModel): ViewModel
}