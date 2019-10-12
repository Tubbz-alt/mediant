package io.numbers.mediant.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.databinding.ActivityBaseBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import timber.log.Timber
import javax.inject.Inject

// Extends from DaggerAppCompatActivity so we do NOT need to write `AndroidInjection.inject(this)`
// in BaseActivity.onCreate() method.
class BaseActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: BaseViewModel

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[BaseViewModel::class.java]
        val binding: ActivityBaseBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_base)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.startMainActivityEvent.observe(this, Observer { Timber.d("go to main") })
    }
}