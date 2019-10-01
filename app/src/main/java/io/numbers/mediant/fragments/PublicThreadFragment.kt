package io.numbers.mediant.fragments

import io.numbers.mediant.R
import io.numbers.mediant.adapters.FeedsAdapter

class PublicThreadFragment : ThreadFragment() {
    override val feedsAdapter = FeedsAdapter(R.layout.feed_public)

    companion object {
        @JvmStatic
        fun newInstance() = PublicThreadFragment()
    }
}