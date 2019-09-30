package io.dt42.mediant.fragments

import io.dt42.mediant.R
import io.dt42.mediant.adapters.FeedsAdapter

class PersonalThreadFragment : ThreadFragment() {
    override val feedsAdapter = FeedsAdapter(R.layout.feed_personal)

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}