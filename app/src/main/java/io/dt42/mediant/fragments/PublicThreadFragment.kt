package io.dt42.mediant.fragments

import io.dt42.mediant.R

class PublicThreadFragment : ThreadFragment() {

    override val layoutResource: Int
        get() = R.layout.feed_public

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}
