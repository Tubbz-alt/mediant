package io.numbers.mediant

import dagger.android.DaggerApplication
import io.numbers.mediant.di.DaggerAppComponent
import timber.log.Timber

class BaseApplication : DaggerApplication() {

    // bind application instance `this` to our app component
    override fun applicationInjector() = DaggerAppComponent.builder().application(this).build()


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}