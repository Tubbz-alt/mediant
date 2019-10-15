package io.numbers.mediant.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentSettingsBinding
import io.numbers.mediant.ui.ItemClickListener
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class SettingsFragment : DaggerFragment(), ItemClickListener {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: SettingsViewModel

    private val adapter = SettingsRecyclerViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settingList.observe(viewLifecycleOwner, Observer {
            adapter.data.replaceAll(it)
        })
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> findNavController().navigate(R.id.action_settingsFragment_to_sharedPreferencesFragment)
            1 -> findNavController().navigate(R.id.action_settingsFragment_to_textileSettingsFragment)
        }
    }
}