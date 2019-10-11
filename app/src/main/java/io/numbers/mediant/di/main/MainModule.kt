package io.numbers.mediant.di.main

import dagger.Module
import dagger.Provides
import io.textile.textile.Textile

@Module
class MainModule {

    // Textile.instance() is ONLY required within MainComponent. Therefore, we do NOT want Textile
    // instance to be provided in Singleton scope.
    @Provides
    fun provideTextileInstance(): Textile = Textile.instance()
}