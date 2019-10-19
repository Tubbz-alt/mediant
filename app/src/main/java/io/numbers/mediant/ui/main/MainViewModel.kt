package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.textile.TextileService
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val textileService: TextileService,
    private val proofModeService: ProofModeService
) : ViewModel(), CoroutineScope by MainScope() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    private lateinit var currentPhotoPath: String

    fun uploadPhoto() = launch(Dispatchers.IO) {
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

    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}