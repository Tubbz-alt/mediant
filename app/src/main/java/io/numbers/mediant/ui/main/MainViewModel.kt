package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PermissionRequestType
import io.numbers.mediant.viewmodel.Event
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val textileService: TextileService,
    private val proofModeService: ProofModeService
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
        try {
            val proofSignatureBundle = proofModeService.generateProofAndSignatures(currentPhotoPath)
            val proofSignatureBundleJson = Gson().toJson(proofSignatureBundle)
            Timber.i(proofSignatureBundleJson)
            textileService.addFile(currentPhotoPath, proofSignatureBundleJson)
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    fun createPhotoFile(directory: File): File =
        File.createTempFile("JPEG_${System.currentTimeMillis()}", ".jpg", directory).apply {
            currentPhotoPath = absolutePath
        }
}