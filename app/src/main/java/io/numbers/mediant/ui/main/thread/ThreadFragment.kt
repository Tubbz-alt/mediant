package io.numbers.mediant.ui.main.thread

import android.os.Bundle
import android.view.LayoutInflater
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
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.databinding.FragmentThreadBinding
import io.numbers.mediant.ui.listeners.DialogListener
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.ui.tab.TabFragment
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.timestampToString
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import io.textile.textile.FeedItemData
import javax.inject.Inject

open class ThreadFragment : DaggerFragment(), TabFragment, FeedItemListener {

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

        adapter = ThreadRecyclerViewAdapter(textileService, this, viewModel.isPersonal)
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
        viewModel.scrollToTopEvent.observe(
            viewLifecycleOwner, EventObserver { smoothScrollToTop() })
    }

    override fun onShowProof(feedItemData: FeedItemData) {
        // TODO: use block API after available: https://github.com/textileio/android-textile/issues/15
        findNavController().navigate(
            if (viewModel.isPersonal) MainFragmentDirections.actionMainFragmentToMediaDetailsFragment(
                textileService.getFileIpfsPath(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption,
                feedItemData.block
            )
            else ThreadFragmentDirections.actionThreadFragmentToMediaDetailsFragment(
                textileService.getFileIpfsPath(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption,
                feedItemData.block
            )
        )
    }

    override fun onPublish(feedItemData: FeedItemData) {
        findNavController().navigate(
            // TODO: use block API after available: https://github.com/textileio/android-textile/issues/15
            MainFragmentDirections.actionMainFragmentToPublishingFragment(
                feedItemData.files.data,
                textileService.getFileIndex(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption
            )
        )
    }

    override fun onDelete(feedItemData: FeedItemData) {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                viewModel.deleteFile(feedItemData.files)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }
        FeedDeletionDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            FeedDeletionDialogFragment::javaClass.name
        )
    }

    override fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }
}