package io.dt42.mediant.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.dt42.mediant.R
import io.dt42.mediant.TextileWrapper
import io.dt42.mediant.model.Post
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.coroutines.*

private const val TAG = "THREAD_FRAGMENT"

abstract class ThreadFragment : Fragment(), CoroutineScope by MainScope() {
    lateinit var name: String
    private val posts = java.util.Collections.synchronizedList(mutableListOf<Post>())

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
            adapter = PostsAdapter(posts)
        }
        swipeRefreshLayout.setOnRefreshListener {
            if (TextileWrapper.isOnline) {
                refreshPosts()
            }
        }
    }

    protected fun refreshPosts() = launch {
        val newPosts = withContext(Dispatchers.IO) {
            try {
                TextileWrapper.fetchPosts(name)
            } catch (e: NoSuchElementException) {
                Log.e(TAG, Log.getStackTraceString(e))
                null
            }
        }
        newPosts?.also {
            val comparator =
                compareByDescending<Post> { it.date.seconds }.thenByDescending { it.date.nanos }
            posts.clear()
            posts.addAll(newPosts.sortedWith(comparator))
            recyclerView.adapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
            recyclerView.adapter?.notifyItemRangeInserted(0, posts.size)
            recyclerView.layoutManager?.scrollToPosition(0)
        }
    }
}