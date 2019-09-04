package io.dt42.mediant.ui.main

import android.os.Bundle
import android.view.View
import io.dt42.mediant.TextileWrapper

class PublicThreadFragment : ThreadFragment() {
    init {
        name = "nbsdev"
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
