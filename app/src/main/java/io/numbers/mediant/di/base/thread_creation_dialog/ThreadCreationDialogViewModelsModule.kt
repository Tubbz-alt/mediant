package io.numbers.mediant.di.base.thread_creation_dialog

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread_list.thread_creation_dialog.ThreadCreationDialogViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadCreationDialogViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadCreationDialogViewModel::class)
    abstract fun bindThreadCreationDialogViewModel(viewModel: ThreadCreationDialogViewModel): ViewModel
}