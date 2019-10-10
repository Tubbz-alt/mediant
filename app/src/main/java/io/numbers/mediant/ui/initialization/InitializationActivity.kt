package io.numbers.mediant.ui.initialization

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.databinding.ActivityInitializationBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in InitializationActivity.onCreate() method.
class InitializationActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: InitializationViewModel

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[InitializationViewModel::class.java]
        val binding: ActivityInitializationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_initialization)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}