package io.numbers.mediant.ui.main.thread_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadListBinding
import io.numbers.mediant.ui.OnItemClickListener
import io.numbers.mediant.ui.OnItemMenuClickListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadListFragment : DaggerFragment(), OnItemClickListener, OnItemMenuClickListener {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadListViewModel

    private val adapter = ThreadListRecyclerViewAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentThreadListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_thread_list, container, false)
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
        MainFragmentDirections.actionMainFragmentToThreadFragment(
            adapter.data[position].id,
            adapter.data[position].name
        ).also { findNavController().navigate(it) }
    }

    override fun onItemMenuClick(position: Int, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_thread_info -> {
            }
            R.id.action_leave_thread -> viewModel.leaveThread(adapter.data[position])
        }
        return true
    }
}