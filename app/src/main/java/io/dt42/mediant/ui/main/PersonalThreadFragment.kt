package io.dt42.mediant.ui.main

import android.os.Bundle
import android.view.View
import io.dt42.mediant.TextileWrapper

class PersonalThreadFragment : ThreadFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TextileWrapper.invokeAfterNodeOnline {
            name = TextileWrapper.profileAddress
            refreshPosts()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
