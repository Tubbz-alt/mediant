package io.numbers.mediant.api

import androidx.lifecycle.MutableLiveData
import io.numbers.mediant.BuildConfig
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile
import io.textile.textile.TextileEventListener
import javax.inject.Inject

class TextileService @Inject constructor(private val textile: Textile) {

    val isNodeOnline = MutableLiveData(textile.isNodeOnline)

    init {
        initThreadApi()
    }

    /**
     * Thread API
     */

    val threadList = MutableLiveData<List<Model.Thread>>()

    private fun initThreadApi() {
        addEventListener(object : BaseTextileEventListener() {
            override fun threadAdded(threadId: String) {
                super.threadAdded(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }

            override fun threadRemoved(threadId: String?) {
                super.threadRemoved(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }
        })
        safelyInvokeIfNodeOnline { loadThreadList() }
    }

    fun loadThreadList() {
        threadList.postValue(textile.threads.list().itemsList)
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
                "${BuildConfig.APPLICATION_ID}.${BuildConfig.VERSION_NAME}.$name.${textile.profile.get().address}.${System.currentTimeMillis()}"
        } while (textile.threads.list().itemsList.any { it.key == key })
        return key
    }

    fun leaveThread(thread: Model.Thread): String = textile.threads.remove(thread.id)

    /**
     * Utils
     */

    // Note that the callback function will be executed on a background thread.
    fun safelyInvokeIfNodeOnline(callback: () -> Unit) {
        if (textile.isNodeOnline) callback()
        else {
            textile.addEventListener(object : BaseTextileEventListener() {
                override fun nodeOnline() {
                    super.nodeOnline()
                    callback()
                }
            })
        }
    }

    fun addEventListener(listener: TextileEventListener) = textile.addEventListener(listener)
}

val Textile.isNodeOnline: Boolean
    get() = try {
        this.online()
    } catch (e: NullPointerException) {
        false
    }