package io.dt42.mediant.fragments

import io.dt42.mediant.R
import io.dt42.mediant.adapters.FeedsAdapter

class PublicThreadFragment : ThreadFragment() {
    override val feedsAdapter = FeedsAdapter(R.layout.feed_public)

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}