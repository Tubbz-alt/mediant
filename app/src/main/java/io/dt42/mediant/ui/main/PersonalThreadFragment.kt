package io.dt42.mediant.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.dt42.mediant.R
import io.dt42.mediant.ui.main.model.Post
import kotlinx.android.synthetic.main.fragment_personal_thread.*

class PersonalThreadFragment : Fragment() {

    // TODO: The posts list should be get from server. We could use thumb image to create loading post animation.
    val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_thread, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PostsAdapter(posts)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
