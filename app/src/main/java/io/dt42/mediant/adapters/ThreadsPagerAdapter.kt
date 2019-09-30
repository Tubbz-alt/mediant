package io.dt42.mediant.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.dt42.mediant.R
import io.dt42.mediant.fragments.PersonalThreadFragment
import io.dt42.mediant.fragments.PublicThreadFragment
import io.dt42.mediant.fragments.ThreadFragment
import io.dt42.mediant.wrappers.TextileWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

data class Tab(val title: Int, val instance: () -> ThreadFragment)

private val TABS = listOf(
    Tab(R.string.feed) { PublicThreadFragment.newInstance() },
    Tab(R.string.storage) { PersonalThreadFragment.newInstance() }
)

class ThreadsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), CoroutineScope by MainScope() {

    private val currentFragments = mutableListOf<ThreadFragment>()

    override fun getItem(position: Int): ThreadFragment = TABS[position].instance.invoke()

    override fun instantiateItem(container: ViewGroup, position: Int) =
        super.instantiateItem(container, position).also { fragment ->
            fragment as ThreadFragment
            when (position) {
                0 -> TextileWrapper.apply {
                    invokeWhenNodeOnline { publicThreadId?.let { launch { fragment.refreshFeeds(it) } } }
                    addOnPublicThreadIdChangedListener {
                        publicThreadId?.let { launch { fragment.refreshFeeds(it) } }
                    }
                    addOnPublicThreadUpdateReceivedListener { launch { fragment.addFeed(it) } }
                }
                1 -> TextileWrapper.apply {
                    invokeWhenNodeOnline { personalThreadId?.let { launch { fragment.refreshFeeds(it) } } }
                    addOnPersonalThreadIdChangedListener {
                        personalThreadId?.let { launch { fragment.refreshFeeds(it) } }
                    }
                    addOnPersonalThreadUpdateReceivedListener { launch { fragment.addFeed(it) } }
                }
            }
            currentFragments.add(position, fragment)
        }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        currentFragments.removeAt(position)
    }

    override fun getPageTitle(position: Int): CharSequence? =
        context.resources.getString(TABS[position].title)

    override fun getCount() = TABS.size

    fun smoothScrollToTop(threadIndex: Int) = currentFragments[threadIndex].smoothScrollToTop()
}