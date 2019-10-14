package io.numbers.mediant.ui.settings

import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {
    val adapter by lazy {
        SettingsRecyclerViewAdapter(
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
}