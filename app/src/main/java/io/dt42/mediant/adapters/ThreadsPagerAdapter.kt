package io.dt42.mediant.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearSmoothScroller
import io.dt42.mediant.R
import io.dt42.mediant.fragments.PersonalThreadFragment
import io.dt42.mediant.fragments.PublicThreadFragment
import io.dt42.mediant.fragments.ThreadFragment
import io.dt42.mediant.wrappers.TextileWrapper
import kotlinx.android.synthetic.main.fragment_thread.*

data class Tab(val title: Int, val instance: () -> ThreadFragment)

private val TABS = listOf(
    Tab(R.string.tab_text_0) { PublicThreadFragment.newInstance() },
    Tab(R.string.tab_text_1) { PersonalThreadFragment.newInstance() }
)

class ThreadsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val currentFragments = mutableListOf<ThreadFragment>()

    override fun getItem(position: Int): ThreadFragment {
        return TABS[position].instance.invoke()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position).also { fragment ->
            fragment as ThreadFragment
            when (position) {
                0 -> TextileWrapper.apply {
                    invokeWhenNodeOnline { publicThreadId?.let { fragment.refreshFeeds(it) } }
                    addOnPublicThreadIdChangedListener {
                        publicThreadId?.let { fragment.refreshFeeds(it) }
                    }
                    addOnPublicThreadUpdateReceivedListener { fragment.addFeed(it) }
                }
                1 -> TextileWrapper.apply {
                    invokeWhenNodeOnline { personalThreadId?.let { fragment.refreshFeeds(it) } }
                    addOnPersonalThreadIdChangedListener {
                        personalThreadId?.let { fragment.refreshFeeds(it) }
                    }
                    addOnPersonalThreadUpdateReceivedListener { fragment.addFeed(it) }
                }
            }
            currentFragments.add(position, fragment)
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

    fun smoothScrollToTop(threadIndex: Int) {
        currentFragments[threadIndex].recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.apply { targetPosition = 0 })
    }
}