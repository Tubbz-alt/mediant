package io.dt42.mediant.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.dt42.mediant.R
import io.dt42.mediant.fragments.PersonalThreadFragment
import io.dt42.mediant.fragments.PublicThreadFragment
import io.dt42.mediant.fragments.ThreadFragment

data class Tab(val title: Int, val instance: () -> ThreadFragment)

private val TABS = listOf(
    Tab(R.string.tab_text_0) { PublicThreadFragment.newInstance() },
    Tab(R.string.tab_text_1) { PersonalThreadFragment.newInstance() }
)

class ThreadsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    val currentFragments = mutableListOf<ThreadFragment>()

    override fun getItem(position: Int): ThreadFragment {
        return TABS[position].instance.invoke()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position).also {
            currentFragments.add(position, it as ThreadFragment)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        currentFragments.removeAt(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABS[position].title)
    }

    override fun getCount(): Int {
        return TABS.size
    }
}