package io.dt42.mediant.ui.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import io.dt42.mediant.R

data class Tab(val title: Int, val instance: Fragment)

private val TABS = listOf(
    Tab(R.string.tab_text_1, PublicThreadFragment.newInstance()),
    Tab(R.string.tab_text_2, PersonalThreadFragment.newInstance())
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    var publicThreadFragment: PublicThreadFragment? = null
    var personalThreadFragment: PersonalThreadFragment? = null

    override fun getItem(position: Int): Fragment {
        return TABS[position].instance
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABS[position].title)
    }

    override fun getCount(): Int {
        return TABS.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val createdFragment = super.instantiateItem(container, position)
        when (position) {
            0 -> publicThreadFragment = createdFragment as PublicThreadFragment
            else -> personalThreadFragment = createdFragment as PersonalThreadFragment
        }
        return createdFragment
    }
}