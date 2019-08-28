package io.dt42.mediant.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.dt42.mediant.R
import io.dt42.mediant.ui.main.model.Post
import kotlinx.android.synthetic.main.fragment_public_thread.*


class PublicThreadFragment : Fragment() {
    private val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_public_thread, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publicRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PostsAdapter(posts)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}
