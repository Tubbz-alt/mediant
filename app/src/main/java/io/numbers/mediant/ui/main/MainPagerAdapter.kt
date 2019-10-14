package io.numbers.mediant.ui.main

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.numbers.mediant.data.Tab

class MainPagerAdapter(
    private val tabs: List<Tab>,
    private val context: Context,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = tabs[position].fragment

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = context.resources.getString(tabs[position].title)

}