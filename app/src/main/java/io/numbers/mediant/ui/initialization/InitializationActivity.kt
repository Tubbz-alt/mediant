package io.numbers.mediant.ui.initialization

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.databinding.ActivityInitializationBinding

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in InitializationActivity.onCreate() method.
class InitializationActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProviders.of(this)[InitializationViewModel::class.java]
        val binding: ActivityInitializationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_initialization)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}