package io.dt42.mediant.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.dt42.mediant.R
import io.dt42.mediant.adapters.PostsAdapter
import io.dt42.mediant.models.Post
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class ThreadFragment : Fragment(), CoroutineScope by MainScope() {
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
    }

//    protected fun refreshPosts(threadId: String) = launch {
//        Log.d(TAG, "Refreshing thread: $threadId")
//        withContext(Dispatchers.IO) {
//            try {
//                TextileWrapper.fetchPosts(threadId)
//            } catch (e: NoSuchElementException) {
//                Log.e(TAG, Log.getStackTraceString(e))
//                null
//            }
//        }?.also { newPosts ->
//            val comparator =
//                compareByDescending<Post> { it.date.seconds }.thenByDescending { it.date.nanos }
//            posts.clear()
//            posts.addAll(newPosts.sortedWith(comparator))
//            recyclerView.adapter?.notifyDataSetChanged()
//            recyclerView.adapter?.notifyItemRangeInserted(0, posts.size)
//            recyclerView.layoutManager?.scrollToPosition(0)
//        }
//        swipeRefreshLayout.isRefreshing = false
//    }
}