package io.numbers.mediant.ui.tab

import dagger.android.support.DaggerFragment

abstract class TabFragment : DaggerFragment() {

    abstract fun smoothScrollToTop()
}