package io.dt42.mediant.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.dt42.mediant.R
import io.dt42.mediant.activities.TAG
import io.dt42.mediant.adapters.FeedsAdapter
import io.dt42.mediant.models.Feed
import io.dt42.mediant.wrappers.TextileWrapper
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import io.textile.textile.Handlers
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class ThreadFragment : Fragment(), CoroutineScope by MainScope() {

    private var threadId: String? = null
    private val feedsAdapter = FeedsAdapter()

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
    ): View? {
        return inflater.inflate(R.layout.fragment_thread, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                refreshFeeds(it)

                // TODO: modify refreshFeeds and addFeed to get the finishing callback
                // TODO: also, scrollToTop when refreshing finished
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    fun refreshFeeds(threadId: String) {
        this.threadId = threadId
        feedsAdapter.feeds.clear()
        TextileWrapper.listFeeds(threadId).forEach {
            addFeed(it)
        }
    }

    fun addFeed(feedItemData: FeedItemData) {
        Log.i(TAG, "Received feed type: ${feedItemData.type}")
        if (feedItemData.type == FeedItemType.FILES) {
            feedItemData.files.apply {
                val fileIndex =
                    if (filesList != null && filesList.size > 0 && filesList[0].index != 0) {
                        filesList[0].index
                    } else {
                        0
                    }
                TextileWrapper.getImageContent(
                    "$data/$fileIndex", 300,
                    object : Handlers.DataHandler {
                        override fun onComplete(data: ByteArray?, media: String?) {
                            if (media == "image/jpeg" || media == "image/png") {
                                addFeed(Feed(user.name, date, data, caption))
                            } else {
                                Log.e(TAG, "Unknown media type: $media")
                            }
                        }

                        override fun onError(e: java.lang.Exception) {
                            Log.e(TAG, Log.getStackTraceString(e))
                        }
                    })
            }
        }
    }

    private fun addFeed(feed: Feed) = activity?.runOnUiThread {
        feedsAdapter.feeds.add(feed)
        // TODO: for better UX, we should show a overlay on the top-edge of the thread showing there
        //   is a update received. We cannot determine if the addFeed is called by user-generated
        //   or cafe-updated feed. Therefore, we should not scrollToTop every time when addFeed called.
    }
}