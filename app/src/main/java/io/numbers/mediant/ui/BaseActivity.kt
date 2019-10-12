package io.numbers.mediant.ui

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in BaseActivity.onCreate() method.
class BaseActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}