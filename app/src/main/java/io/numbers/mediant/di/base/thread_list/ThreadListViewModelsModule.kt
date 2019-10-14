package io.numbers.mediant.di.base.thread_list

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread_list.ThreadListViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadListViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadListViewModel::class)
    abstract fun bindThreadListViewModel(viewModel: ThreadListViewModel): ViewModel
}