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
import io.textile.textile.FeedItemData
import io.textile.textile.Textile
import io.textile.textile.TextileEventListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"
// TODO: implement infinite recycler view to reduce this limit
private const val REQUEST_LIMIT = 999

class TextileService @Inject constructor(
    private val textile: Textile,
    private val sharedPreferences: SharedPreferences,
    private val application: Application
) {

    init {
        initNodeStatusLiveDataListeners()
        initThreadLiveDataListeners()
        initFeedLiveDataListeners()
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

    private fun initNodeStatusLiveDataListeners() {
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

    private fun initThreadLiveDataListeners() {
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
     * Feeds
     */

    val feedMap: LiveData<Map<String, MutableLiveData<out List<FeedItemData>>>> =
        Transformations.map(threadList) { list ->
            list.map { it.id to MutableLiveData(listFeeds(it.id)) }.toMap()
        }

    private fun initFeedLiveDataListeners() {
        textile.addEventListener(object : BaseTextileEventListener() {
        })
    }

    private fun listFeeds(threadId: String) = View.FeedRequest.newBuilder()
        .setThread(threadId)
        .setLimit(REQUEST_LIMIT)
        .build()
        .let { textile.feed.list(it) }

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

class TextileInfoListener : BaseTextileEventListener() {

    private val prefix = "--------------->"

    override fun nodeStarted() {
        super.nodeStarted()
        Timber.i("$prefix node started")
    }

    override fun nodeFailedToStart(e: Exception) {
        super.nodeFailedToStart(e)
        Timber.e(e)
    }

    override fun nodeStopped() {
        super.nodeStopped()
        Timber.i("$prefix node stopped")
    }

    override fun nodeFailedToStop(e: Exception) {
        super.nodeFailedToStop(e)
        Timber.e(e)
    }

    override fun nodeOnline() {
        super.nodeOnline()
        Timber.i("$prefix node online")
    }

    override fun willStopNodeInBackgroundAfterDelay(seconds: Int) {
        super.nodeOnline()
        Timber.i("$prefix will stop node in background after $seconds second(s)")
    }

    override fun canceledPendingNodeStop() {
        super.canceledPendingNodeStop()
        Timber.i("$prefix canceled pending node stop")
    }

    override fun notificationReceived(notification: Model.Notification) {
        super.notificationReceived(notification)
        Timber.i("$prefix notification received: ${notification.id}")
    }

    override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
        super.threadUpdateReceived(threadId, feedItemData)
        Timber.i("$prefix thread update received: $threadId (${feedItemData.type})")
    }

    override fun threadAdded(threadId: String) {
        super.threadAdded(threadId)
        Timber.i("$prefix thread added: $threadId")
    }

    override fun threadRemoved(threadId: String) {
        super.threadRemoved(threadId)
        Timber.i("$prefix thread remove: $threadId")
    }

    override fun accountPeerAdded(peerId: String) {
        super.accountPeerAdded(peerId)
        Timber.i("$prefix account peer added: $peerId")
    }

    override fun accountPeerRemoved(peerId: String) {
        super.accountPeerRemoved(peerId)
        Timber.i("$prefix account peer removed: $peerId")
    }

    override fun queryDone(queryId: String) {
        super.queryDone(queryId)
        Timber.i("$prefix query done: $queryId")
    }

    override fun queryError(queryId: String, e: Exception) {
        super.queryError(queryId, e)
        Timber.e("$prefix query error: $queryId")
        Timber.e(e)
    }

    override fun clientThreadQueryResult(queryId: String, thread: Model.Thread) {
        super.clientThreadQueryResult(queryId, thread)
        Timber.i("$prefix client thread query result: $queryId (thread ID: ${thread.id})")
    }

    override fun contactQueryResult(queryId: String, contact: Model.Contact) {
        super.contactQueryResult(queryId, contact)
        Timber.i("$prefix contact query result: $queryId (contact address: ${contact.address})")
    }

    override fun syncUpdate(status: Model.CafeSyncGroupStatus) {
        super.syncUpdate(status)
        val progress =
            if (status.groupsSizeTotal > 0) status.groupsSizeComplete * 100 / status.groupsSizeTotal
            else 0
        Timber.i("$prefix sync update: ${progress}% ${status.id}")
    }

    override fun syncComplete(status: Model.CafeSyncGroupStatus) {
        super.syncComplete(status)
        Timber.i("$prefix sync complete: ${status.id}")
    }

    override fun syncFailed(status: Model.CafeSyncGroupStatus) {
        super.syncFailed(status)
        Timber.e("$prefix sync failed: ${status.id}")
    }
}