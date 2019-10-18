package io.numbers.mediant.ui.media_details

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import javax.inject.Inject

class MediaDetailsViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService
) : ViewModel() {

    val imageIpfsPath = MutableLiveData("")
    val imageDrawable = MediatorLiveData<Drawable>()
    val userName = MutableLiveData("")
    val blockTimestamp = MutableLiveData("")
    val proof = MutableLiveData("")
    val proofSignature = MutableLiveData("")
    val mediaSignature = MutableLiveData("")
    val blockHash = MutableLiveData("")

    init {
        imageDrawable.addSource(imageIpfsPath) { updateImage(it) }
    }

    private fun updateImage(ipfsPath: String) {
        textileService.getImageContent(ipfsPath) {
            imageDrawable.postValue(
                BitmapDrawable(
                    application.resources,
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                )
            )
        }
    }
}