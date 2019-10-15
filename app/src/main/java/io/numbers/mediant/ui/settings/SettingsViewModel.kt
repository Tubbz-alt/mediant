package io.numbers.mediant.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import io.numbers.mediant.data.SettingItem
import io.numbers.mediant.ui.OnItemClickListener
import io.numbers.mediant.util.Event
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel(), OnItemClickListener {

    val navToSharedPreferencesFragmentEvent = MutableLiveData<Event<Unit>>()
    val navToTextileSettingsFragmentEvent = MutableLiveData<Event<Unit>>()

    val adapter = SettingsRecyclerViewAdapter(
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
        ), this
    )

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> navToSharedPreferencesFragmentEvent.value = Event(Unit)
            1 -> navToTextileSettingsFragmentEvent.value = Event(Unit)
        }
    }
}