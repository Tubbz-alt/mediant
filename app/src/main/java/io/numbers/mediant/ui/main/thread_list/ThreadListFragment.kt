package io.numbers.mediant.ui.main.thread_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadListBinding
import io.numbers.mediant.ui.listeners.DialogListener
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.numbers.mediant.ui.listeners.ItemMenuClickListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.ui.main.thread_list.thread_creation_dialog.ThreadCreationDialogFragment
import io.numbers.mediant.ui.tab.TabFragment
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadListFragment : DaggerFragment(), TabFragment, ItemClickListener, ItemMenuClickListener {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadListViewModel

    private val adapter = ThreadListRecyclerViewAdapter(this, this)

    private lateinit var binding: FragmentThreadListBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.data = it })
        viewModel.openDialog.observe(
            viewLifecycleOwner, EventObserver { showThreadCreationDialog() })
    }

    private fun showThreadCreationDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                val threadName =
                    (dialog as ThreadCreationDialogFragment).viewModel.threadName.value ?: ""
                viewModel.addThread(threadName)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }

        ThreadCreationDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadCreationDialogFragment::javaClass.name
        )
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

    override fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }
}