package io.dt42.mediant.fragments

import android.os.Bundle
import android.view.View
import io.dt42.mediant.wrappers.TextileWrapper
import kotlinx.android.synthetic.main.fragment_thread.*

class PublicThreadFragment : ThreadFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            if (TextileWrapper.isOnline) {
                TextileWrapper.publicThreadId?.also { refreshPosts(it) }
                swipeRefreshLayout.isRefreshing = false
            }
        }
        TextileWrapper.invokeAfterPublicThreadIdChanged {
            refreshPosts(it)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}
