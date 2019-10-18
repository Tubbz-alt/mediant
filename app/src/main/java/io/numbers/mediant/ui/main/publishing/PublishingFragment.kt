package io.numbers.mediant.ui.main.publishing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentPublishingBinding
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class PublishingFragment : DaggerFragment(), ItemClickListener {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: PublishingViewModel

    private val adapter = PublishingRecyclerViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[PublishingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPublishingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_publishing, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.data = it })
    }

    override fun onItemClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}