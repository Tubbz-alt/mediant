package io.dt42.mediant.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import io.dt42.mediant.activities.TAG
import io.dt42.mediant.wrappers.TextileWrapper
import kotlinx.android.synthetic.main.fragment_thread.*

class PublicThreadFragment : ThreadFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            if (TextileWrapper.isOnline) {
                TextileWrapper.publicThreadId.also {
                    if (it != null) {
                        refreshPosts(it)
                    } else {
                        Log.e(TAG, "personalThreadId has not initialized")
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            } else {
                Log.e(TAG, "node is offline")
                swipeRefreshLayout.isRefreshing = false
            }
        }
        TextileWrapper.invokeAfterPublicThreadIdChanged {
            refreshPosts(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}
