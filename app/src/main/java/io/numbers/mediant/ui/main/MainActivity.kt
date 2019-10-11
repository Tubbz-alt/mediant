package io.numbers.mediant.ui.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import io.numbers.mediant.BaseActivity
import io.numbers.mediant.R
import io.numbers.mediant.databinding.ActivityMainBinding
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    // TODO: possibly to move the factory to base activity?
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[MainViewModel::class.java]
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}