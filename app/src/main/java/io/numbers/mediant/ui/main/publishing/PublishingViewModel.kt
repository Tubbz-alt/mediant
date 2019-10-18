package io.numbers.mediant.ui.main.publishing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import javax.inject.Inject

class PublishingViewModel @Inject constructor(
    private val textileService: TextileService
) : ViewModel() {

    val threadList = textileService.threadList
    val isLoading = MutableLiveData(false)

    init {
        loadThreadList()
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }
}