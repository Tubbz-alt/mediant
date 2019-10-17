package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.data.Tab
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PermissionRequestType
import io.numbers.mediant.viewmodel.Event
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val tabs: List<Tab>,
    private val permissionManager: PermissionManager
) : ViewModel() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    val navToPermissionRationaleFragmentEvent = MutableLiveData<Event<Int>>()

    fun prepareCamera() {
        if (permissionManager.hasPermissions(PermissionRequestType.PROOFMODE)) {
            Timber.i("open camera")
        } else if (!permissionManager.askPermissions(PermissionRequestType.PROOFMODE)) {
            navToPermissionRationaleFragmentEvent.value =
                Event(PermissionRequestType.PROOFMODE.value.rationale)
        }
    }
}