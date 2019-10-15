package io.numbers.mediant.api

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.R
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile
import io.textile.textile.TextileEventListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"

class TextileService @Inject constructor(
    private val textile: Textile,
    private val sharedPreferences: SharedPreferences,
    private val application: Application
) {

    init {
        initNodeStatusApi()
        initThreadApi()
    }

    /**
     * Initialization
     */

    private val textilePath by lazy {
        File(application.applicationContext.filesDir, TEXTILE_FOLDER_NAME).absolutePath
    }

    val hasLaunched = MutableLiveData(false)
    val isNodeOnline = MutableLiveData(textile.isNodeOnline)

    fun launch() {
        hasLaunched.value = true
        Textile.launch(application.applicationContext, textilePath, false)
    }

    fun hasInitialized(): Boolean = Textile.isInitialized(textilePath)

    fun createNewWalletAndAccount(): String {
        val phrase = Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
        sharedPreferences.edit().putString(
            application.resources.getString(R.string.key_wallet_recovery_phrase),
            phrase
        ).apply()
        Timber.i("Create new wallet: $phrase")
        return phrase
    }

    private fun initNodeStatusApi() {
        addEventListener(object : BaseTextileEventListener() {
            override fun nodeOnline() {
                super.nodeOnline()
                isNodeOnline.postValue(true)
            }

            override fun nodeStopped() {
                super.nodeStopped()
                isNodeOnline.postValue(false)
            }
        })
    }

    /**
     * Thread
     */

    private val threadList = MutableLiveData<List<Model.Thread>>()
    val publicThreadList: LiveData<List<Model.Thread>> = Transformations.map(threadList) { list ->
        list.filter {
            it.id != sharedPreferences.getString(
                application.resources.getString(R.string.key_personal_thread_id), null
            )
        }
    }

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
        safelyInvokeIfNodeOnline {
            initPersonalThread()
            loadThreadList()
        }
    }

    private fun initPersonalThread() {
        try {
            val personalThreadId = sharedPreferences.getString(
                application.resources.getString(R.string.key_personal_thread_id), null
            )
            textile.threads.get(personalThreadId)
            Timber.i("Personal thread has already been created: $personalThreadId")
        } catch (e: Exception) {
            addThread(
                textile.profile.name(),
                Model.Thread.Type.PRIVATE,
                Model.Thread.Sharing.NOT_SHARED
            ).also {
                sharedPreferences.edit().putString(
                    application.resources.getString(R.string.key_personal_thread_id), it.id
                ).apply()
                Timber.i("Create personal thread: ${it.id}")
            }
        }
    }

    fun loadThreadList() = threadList.postValue(textile.threads.list().itemsList)

    fun addThread(
        threadName: String,
        type: Model.Thread.Type,
        sharing: Model.Thread.Sharing
    ): Model.Thread {
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setPreset(View.AddThreadConfig.Schema.Preset.MEDIA)
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey(generateThreadKey(threadName))
            .setName(threadName)
            .setType(type)
            .setSharing(sharing)
            .setSchema(schema)
            .build()
        return textile.threads.add(config)
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