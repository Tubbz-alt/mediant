package io.numbers.mediant.di.base.permission_rationale

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.permission_rationale.PermissionRationaleViewModel

@Suppress("UNUSED")
@Module
abstract class PermissionRationaleViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PermissionRationaleViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: PermissionRationaleViewModel): ViewModel
}