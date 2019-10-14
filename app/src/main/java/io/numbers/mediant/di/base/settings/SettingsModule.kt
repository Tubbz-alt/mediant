package io.numbers.mediant.di.base.settings

import dagger.Module
import dagger.Provides
import io.numbers.mediant.R
import io.numbers.mediant.data.SettingItem
import io.numbers.mediant.ui.settings.SettingsRecyclerViewAdapter

@Module
class SettingsModule {
    @Provides
    fun provideSettingsRecyclerViewAdapter() = SettingsRecyclerViewAdapter(
        listOf(
            SettingItem(
                R.string.general,
                R.string.general_settings_summary,
                R.drawable.ic_person_black_24dp
            ),
            SettingItem(
                R.string.textile,
                R.string.textile_settings_summary,
                R.drawable.ic_leak_add_black_24dp
            )
        )
    )
}