package io.numbers.mediant.di.base.main

import dagger.Module
import dagger.Provides
import io.numbers.mediant.R
import io.numbers.mediant.ui.main.personal_thread.PersonalThreadFragment
import io.numbers.mediant.ui.main.thread_list.ThreadListFragment
import io.numbers.mediant.ui.tab.Tab

@Module
class MainModule {

    @Provides
    fun provideTabList() =
        listOf(
            Tab(R.string.feeds, ThreadListFragment()),
            Tab(R.string.storage, PersonalThreadFragment())
        )
}