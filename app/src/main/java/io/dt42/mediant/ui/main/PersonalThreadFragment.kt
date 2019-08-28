package io.dt42.mediant.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.dt42.mediant.R
import io.dt42.mediant.TextileWrapper
import io.dt42.mediant.ui.main.model.Post
import io.textile.textile.Handlers
import kotlinx.android.synthetic.main.fragment_personal_thread.*
import kotlin.concurrent.thread

private const val TAG = "PERSONAL_THREAD"

class PersonalThreadFragment : Fragment() {
    private val posts = mutableListOf<Post>()

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
    }

    private fun refreshPosts() {
        thread {
            posts.clear()
            personalRecyclerView.adapter?.notifyDataSetChanged()
            val newPostsCount = fetchPosts()
            activity?.runOnUiThread {
                personalSwipeRefreshLayout.isRefreshing = false
                personalRecyclerView.adapter?.notifyItemRangeInserted(0, newPostsCount)
                personalRecyclerView.layoutManager?.scrollToPosition(0)
            }
        }
    }

    private fun fetchPosts(): Int {
        var counter = 0
        TextileWrapper.getImageList()?.forEach { files ->
            files.filesList.forEach {
                TextileWrapper.fetchImageContent(
                    it.linksMap["large"]?.hash,
                    object : Handlers.DataHandler {
                        override fun onComplete(data: ByteArray?, media: String?) {
                            if (media == "image/jpeg") {
                                posts.add(Post(files.user.address, data, files.caption))
                                counter++
                            }
                        }

                        override fun onError(e: Exception?) {
                            Log.getStackTraceString(e)
                        }
                    })
            }
        }
        return counter
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
