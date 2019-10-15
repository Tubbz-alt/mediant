package io.numbers.mediant.ui.main.thread_list.thread_creation_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ThreadCreationDialogViewModel @Inject constructor() : ViewModel() {

    val threadName = MutableLiveData("")
}