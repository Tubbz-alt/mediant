package io.numbers.mediant.ui.publishing

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import timber.log.Timber
import javax.inject.Inject

class PublishingViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService
) : ViewModel() {

    val dataHash = MutableLiveData("")
    val fileIndex = MutableLiveData(0)
    val imageDrawable = MediatorLiveData<Drawable>()
    val userName = MutableLiveData("")
    val date = MutableLiveData("")
    val caption = MutableLiveData("")
    val threadList = textileService.publicThreadList
    val isLoading = MutableLiveData(false)

    init {
        loadThreadList()
        imageDrawable.addSource(dataHash) { if (!it.isNullOrEmpty()) updateImage("$it/${fileIndex.value}") }
        imageDrawable.addSource(fileIndex) { if (!dataHash.value.isNullOrEmpty()) updateImage("${dataHash.value}/$it") }
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
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

    fun publishFile(threadId: String) {
        dataHash.value?.also { hash ->
            textileService.shareFile(hash, caption.value ?: "", threadId) {
                Timber.i("shared: ${it.id}")
            }
        }
    }

}