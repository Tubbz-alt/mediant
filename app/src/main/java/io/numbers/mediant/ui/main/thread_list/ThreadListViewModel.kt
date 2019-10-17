package io.numbers.mediant.ui.main.thread_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.viewmodel.Event
import io.textile.pb.Model
import javax.inject.Inject

class ThreadListViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val threadList = textileService.publicThreadList
    val isLoading = MutableLiveData(false)
    val openDialog = MutableLiveData<Event<Unit>>()

    init {
        loadThreadList()
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }

    fun createThread() {
        openDialog.value = Event(Unit)
    }

    fun addThread(name: String) =
        textileService.addThread(name, Model.Thread.Type.OPEN, Model.Thread.Sharing.SHARED)

    fun leaveThread(thread: Model.Thread) = textileService.leaveThread(thread)
}