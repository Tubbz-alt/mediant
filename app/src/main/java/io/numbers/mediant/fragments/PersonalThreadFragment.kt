package io.numbers.mediant.fragments

import io.numbers.mediant.R
import io.numbers.mediant.adapters.FeedsAdapter

class PersonalThreadFragment : ThreadFragment() {
    override val feedsAdapter = FeedsAdapter(R.layout.feed_personal)

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}