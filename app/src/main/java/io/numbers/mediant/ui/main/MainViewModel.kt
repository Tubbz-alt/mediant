package io.numbers.mediant.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import io.numbers.mediant.util.Event
import io.textile.textile.Textile
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val textile: Textile
) : ViewModel() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    val navToSettingsFragmentEvent: LiveData<Event<Boolean>> =
        Transformations.map(selectedOptionsItem) {
            Event(it == R.id.menuItemNavToSettings)
        }
    val text = MutableLiveData("logProfile")

    fun onClick() = try {
        Timber.d("${textile.profile.get()}")
    } catch (e: Exception) {
        Timber.e(e)
    }
}