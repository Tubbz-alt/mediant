package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PermissionRequestType
import io.numbers.mediant.viewmodel.Event
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val textileService: TextileService
) : ViewModel() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    val openCameraEvent = MutableLiveData<Event<Unit>>()
    val navToPermissionRationaleFragmentEvent = MutableLiveData<Event<Int>>()
    private lateinit var currentPhotoPath: String

    fun prepareCamera() {
        if (permissionManager.hasPermissions(PermissionRequestType.PROOFMODE)) {
            openCameraEvent.value = Event(Unit)
        } else if (!permissionManager.askPermissions(PermissionRequestType.PROOFMODE)) {
            navToPermissionRationaleFragmentEvent.value =
                Event(PermissionRequestType.PROOFMODE.value.rationale)
        }
    }

    fun uploadPhoto() {
        textileService.addFile(currentPhotoPath)
    }

    fun createPhotoFile(directory: File): File =
        File.createTempFile("JPEG_${System.currentTimeMillis()}", ".jpg", directory).apply {
            currentPhotoPath = absolutePath
        }
}