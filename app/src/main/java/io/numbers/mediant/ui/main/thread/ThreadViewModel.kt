package io.numbers.mediant.ui.main.thread

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ThreadViewModel @Inject constructor() : ViewModel() {

    val threadId = MutableLiveData("id")
}