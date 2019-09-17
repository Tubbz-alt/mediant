package io.dt42.mediant.fragments

import android.os.Bundle
import android.view.View
import io.dt42.mediant.wrappers.TextileWrapper
import kotlinx.android.synthetic.main.fragment_thread.*

class PersonalThreadFragment : ThreadFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            if (TextileWrapper.isOnline) {
                TextileWrapper.personalThreadId?.also { refreshPosts(it) }
                swipeRefreshLayout.isRefreshing = false
            }
        }
        TextileWrapper.invokeAfterPersonalThreadIdChanged {
            refreshPosts(it)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
