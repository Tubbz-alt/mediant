package io.numbers.mediant.di.base

import dagger.Module
import dagger.Provides
import io.numbers.mediant.ui.BaseActivity
import io.numbers.mediant.util.PermissionManager

@Module
class BaseModule {

    @Provides
    fun providePermissionManager(baseActivity: BaseActivity) = PermissionManager(baseActivity)
}