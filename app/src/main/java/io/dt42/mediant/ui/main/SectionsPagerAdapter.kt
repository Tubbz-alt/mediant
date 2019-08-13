package io.dt42.mediant.ui.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import io.dt42.mediant.R

data class Tab(val title: Int, val instance: Fragment)

private val TABS = listOf(
    Tab(R.string.tab_text_0, PublicThreadFragment.newInstance()),
    Tab(R.string.tab_text_1, PersonalThreadFragment.newInstance())
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // TODO: remove these reference after we can get photos from Textile server
    lateinit var publicThreadFragment: PublicThreadFragment
    lateinit var personalThreadFragment: PersonalThreadFragment

    override fun getItem(position: Int): Fragment {
        return TABS[position].instance
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABS[position].title)
    }

    override fun getCount(): Int {
        return TABS.size
    }

    /**
     * TODO
     * Currently, if a user use external camera app to take picture in different orientation to main activity, the app
     * will crash due to main activity has been recreated and [personalThreadFragment] has not been initialized. This
     * error would not be a problem if we only display photos from Textile server. Also, we can remove following method
     * [instantiateItem] after we can get photos from Textile server only.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val createdFragment = super.instantiateItem(container, position)
        when (position) {
            0 -> publicThreadFragment = createdFragment as PublicThreadFragment
            else -> personalThreadFragment = createdFragment as PersonalThreadFragment
        }
        return createdFragment
    }
}