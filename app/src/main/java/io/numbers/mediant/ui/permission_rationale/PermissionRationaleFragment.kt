package io.numbers.mediant.ui.permission_rationale

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentPermissionRationaleBinding
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class PermissionRationaleFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: PermissionRationaleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[PermissionRationaleViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPermissionRationaleBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_permission_rationale, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.rationale.value =
            arguments?.let { PermissionRationaleFragmentArgs.fromBundle(it).rationale }
        viewModel.openAppSettingsEvent.observe(viewLifecycleOwner, EventObserver {
            Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            ).also { startActivity(it) }

            // Finish application when user go to the setting page. This is because user may disable
            // some permissions, which will cause the application restart without initialize Textile
            // node (i.e. start from initialization fragment).
            activity?.finish()
        })
    }
}