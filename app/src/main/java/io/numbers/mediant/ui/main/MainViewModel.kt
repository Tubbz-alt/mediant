package io.numbers.mediant.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import io.numbers.mediant.data.Tab
import io.numbers.mediant.viewmodel.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(val tabs: List<Tab>) : ViewModel() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    val navToSettingsFragmentEvent: LiveData<Event<Boolean>> =
        Transformations.map(selectedOptionsItem) {
            Event(it == R.id.menuItemNavToSettings)
        }
}