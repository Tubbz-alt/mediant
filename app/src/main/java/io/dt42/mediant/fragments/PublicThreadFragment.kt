package io.dt42.mediant.fragments

import android.os.Bundle
import android.view.View
import io.dt42.mediant.wrappers.TextileWrapper

const val PUBLIC_THREAD_NAME = "nbsdev"

class PublicThreadFragment : ThreadFragment() {
    init {
        name = PUBLIC_THREAD_NAME
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TextileWrapper.invokeAfterNodeOnline { refreshPosts() }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}
