package io.numbers.mediant.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.numbers.mediant.BaseApplication
import javax.inject.Singleton

@Singleton // AppComponent owns the @Singleton scope.
@Component(
    modules = [
        AndroidInjectionModule::class, // required for all Dagger for Android application
        AppModule::class,
        ActivityBuildersModule::class,
        ViewModelFactoryModule::class
    ]
) // BaseApplication is a client of AppComponent service
interface AppComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        // use BindsInstance because we want AppComponent to exist across the entire lifetime of the BaseApplication
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}