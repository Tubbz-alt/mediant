package io.dt42.mediant.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.dt42.mediant.R
import io.dt42.mediant.TextileWrapper
import io.dt42.mediant.model.Post
import kotlinx.android.synthetic.main.fragment_personal_thread.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PersonalThreadFragment : Fragment() {
    private val posts = java.util.Collections.synchronizedList(mutableListOf<Post>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_thread, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalSwipeRefreshLayout.setOnRefreshListener { refreshPosts() }
        personalRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PostsAdapter(posts)
        }
        TextileWrapper.invokeAfterNodeOnline { refreshPosts() }
    }

    private fun refreshPosts() = GlobalScope.launch {
        val deferred =
            async { TextileWrapper.fetchPosts(TextileWrapper.profileAddress, limit = 20) }
        val newPosts = deferred.await()
        val comparator =
            compareByDescending<Post> { it.date.seconds }.thenByDescending { it.date.nanos }
        posts.clear()
        posts.addAll(newPosts.sortedWith(comparator))
        activity?.runOnUiThread {
            personalRecyclerView.adapter?.notifyDataSetChanged()
            personalSwipeRefreshLayout.isRefreshing = false
            personalRecyclerView.adapter?.notifyItemRangeInserted(0, posts.size)
            personalRecyclerView.layoutManager?.scrollToPosition(0)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
