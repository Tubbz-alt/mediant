package io.dt42.mediant.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.dt42.mediant.R

data class Tab(val title: Int, val instance: Fragment)

private val TABS = listOf(
    Tab(R.string.tab_text_0, PublicThreadFragment.newInstance()),
    Tab(R.string.tab_text_1, PersonalThreadFragment.newInstance())
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return TABS[position].instance
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABS[position].title)
    }

    override fun getCount(): Int {
        return TABS.size
    }
}