package io.numbers.mediant.ui.main.thread

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.databinding.FragmentThreadBinding
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

open class ThreadFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadViewModel

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var textileService: TextileService

    private lateinit var adapter: ThreadRecyclerViewAdapter
    protected lateinit var binding: FragmentThreadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setThreadIdToViewModel()

        adapter = ThreadRecyclerViewAdapter(textileService)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    private fun setThreadIdToViewModel() {
        val threadId = arguments?.let { ThreadFragmentArgs.fromBundle(it).threadId }
            ?: preferenceHelper.personalThreadId
        threadId?.also { viewModel.setThreadId(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.feedList.observe(viewLifecycleOwner, Observer { adapter.data = it })
    }
}