package io.dt42.mediant.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.dt42.mediant.R
import io.dt42.mediant.activities.TAG
import io.dt42.mediant.adapters.FeedsAdapter
import io.dt42.mediant.wrappers.TextileWrapper
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.coroutines.*

abstract class ThreadFragment : Fragment(), CoroutineScope by MainScope() {

    private var threadId: String? = null
    abstract val feedsAdapter: FeedsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedsAdapter.context = activity
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = feedsAdapter
        }
        swipeRefreshLayout.setOnRefreshListener(createRefreshListener())
    }

    private fun createRefreshListener() = SwipeRefreshLayout.OnRefreshListener {
        threadId.also {
            if (it == null) {
                swipeRefreshLayout.isRefreshing = false
                val msg = "This thread has not been initialized."
                Log.e(TAG, msg)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            } else {
                launch {
                    refreshFeeds(it)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    suspend fun refreshFeeds(threadId: String) = withContext(Dispatchers.IO) {
        this@ThreadFragment.threadId = threadId
        TextileWrapper.snapshotAllThreads()
        TextileWrapper.checkCafeMessagesAsync()
        withContext(Dispatchers.Main) { feedsAdapter.feeds.clear() }
        TextileWrapper.listFeeds(threadId).forEachIndexed { index, it ->
            Log.i(TAG, "Feed ($index)\ttype: ${it.type}\tblock: ${it.block}")
            addFeed(it)
        }
    }

    suspend fun addFeed(feedItemData: FeedItemData) = withContext(Dispatchers.Main) {
        if (feedItemData.type == FeedItemType.FILES) {
            if (feedsAdapter.feeds.add(feedItemData) == 0) smoothScrollToTop()
        }
    }

    fun smoothScrollToTop() = recyclerView.layoutManager?.startSmoothScroll(object :
        LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference() = SNAP_TO_START
    }.apply { targetPosition = 0 })
}