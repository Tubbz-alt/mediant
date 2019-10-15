package io.numbers.mediant.ui.main.thread_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.BuildConfig.APPLICATION_ID
import io.numbers.mediant.BuildConfig.VERSION_NAME
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile
import javax.inject.Inject

class ThreadListViewModel @Inject constructor(private val textile: Textile) : ViewModel() {

    val threadList = MutableLiveData(textile.threads.list().itemsList)
    val isLoading = MutableLiveData(false)

    init {
        textile.addEventListener(object : BaseTextileEventListener() {
            override fun threadAdded(threadId: String) {
                super.threadAdded(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }

            override fun threadRemoved(threadId: String?) {
                super.threadRemoved(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }
        })
        loadThreadList()
    }

    fun loadThreadList() {
        isLoading.value = true
        threadList.value = textile.threads.list().itemsList
        isLoading.value = false
    }

    fun addThread() {
        val name = "placeholder"
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setPreset(View.AddThreadConfig.Schema.Preset.MEDIA)
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey(generateThreadKey(name))
            .setName(name)
            .setType(Model.Thread.Type.OPEN)
            .setSharing(Model.Thread.Sharing.SHARED)
            .setSchema(schema)
            .build()
        textile.threads.add(config)
    }

    private fun generateThreadKey(name: String): String {
        var key: String
        do {
            key =
                "$APPLICATION_ID.$VERSION_NAME.$name.${textile.profile.get().address}.${System.currentTimeMillis()}"
        } while (textile.threads.list().itemsList.any { it.key == key })
        return key
    }

    fun leaveThread(thread: Model.Thread) {
        textile.threads.remove(thread.id)
    }
}