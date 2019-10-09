package io.numbers.mediant.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.numbers.mediant.App

@Component(
    modules = [AndroidInjectionModule::class, // required for all Dagger for Android application
        AppModule::class,
        ActivityBuildersModule::class]
)
interface AppComponent : AndroidInjector<App> {  // App is a client of AppComponent service

    @Component.Builder
    interface Builder {

        // use BindsInstance because we want AppComponent to exist across the entire lifetime of the App
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}