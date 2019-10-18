package io.numbers.mediant.ui.publishing

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

        arguments?.let {
            viewModel.dataHash.value = PublishingFragmentArgs.fromBundle(it).dataHash
            viewModel.fileIndex.value = PublishingFragmentArgs.fromBundle(it).fileIndex
            viewModel.userName.value = PublishingFragmentArgs.fromBundle(it).userName
            viewModel.date.value = PublishingFragmentArgs.fromBundle(it).date
            viewModel.caption.value = PublishingFragmentArgs.fromBundle(it).caption
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.data = it })
    }

    override fun onItemClick(position: Int) = viewModel.publishFile(adapter.data[position].id)
}