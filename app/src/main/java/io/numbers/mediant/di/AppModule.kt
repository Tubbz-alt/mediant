package io.numbers.mediant.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.PreferenceHelper
import io.textile.textile.Textile
import javax.inject.Singleton

// Provides all application-level dependencies, such as Retrofit, Textile, etc.

@Module
class AppModule {
    // Generally provide dependencies with @Singleton annotation due to AppComponent owns the
    // singleton scope and Dagger will check which providers also OWNS the singleton scope.
    // Therefore, actually we can use any name of scope to annotate AppComponent and the providers
    // in AppModule. For example,
    // ```
    // @Singleton
    // @Provides
    // fun provideSomeString() = "some string"
    // ```

    @Singleton
    @Provides
    fun providePreferenceHelper(application: Application) = PreferenceHelper(application)

    @Singleton
    @Provides
    fun provideTextileService(
        preferenceHelper: PreferenceHelper,
        application: Application
    ) = TextileService(Textile.instance(), preferenceHelper, application)

    @Singleton
    @Provides
    fun provideProofModeService(application: Application) = ProofModeService(application)
}