package io.dt42.mediant.fragments

import io.dt42.mediant.R

class PersonalThreadFragment : ThreadFragment() {

    override val layoutResource: Int
        get() = R.layout.feed_personal

    companion object {
        @JvmStatic
        fun newInstance() = PersonalThreadFragment()
    }
}
