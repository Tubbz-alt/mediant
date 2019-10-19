package io.numbers.mediant.api.textile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.util.PreferenceHelper
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"
// TODO: implement infinite recycler view to reduce this limit
private const val REQUEST_LIMIT = 999

// TODO: replace Timber.e with throw (handle exception by showing snackbar)
class TextileService @Inject constructor(
    private val textile: Textile,
    private val preferenceHelper: PreferenceHelper,
    private val application: Application
) {

    val hasLaunched = MutableLiveData(false)
    val isNodeOnline = MutableLiveData(textile.isNodeOnline)

    val threadList = MutableLiveData<List<Model.Thread>>()
    val publicThreadList: LiveData<List<Model.Thread>> = Transformations.map(threadList) { list ->
        list.filter { it.id != preferenceHelper.personalThreadId }
    }

    private val feedItemSubtype: EnumSet<FeedItemType> =
        EnumSet.of(FeedItemType.FILES, FeedItemType.JOIN, FeedItemType.IGNORE)

    init {
        initNodeStatusLiveDataListeners()
        initThreadLiveDataListeners()
    }

    /**
     * Initialization
     */

    private val textilePath by lazy {
        File(
            application.applicationContext.filesDir,
            TEXTILE_FOLDER_NAME
        ).absolutePath
    }

    fun launch() {
        hasLaunched.value = true
        Textile.launch(application.applicationContext, textilePath, false)
    }

    fun hasInitialized(): Boolean = Textile.isInitialized(textilePath)

    fun createNewWalletAndAccount(): String {
        val phrase = Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
        preferenceHelper.walletRecoveryPhrase = phrase
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

    private fun initThreadLiveDataListeners() {
        addEventListener(object : BaseTextileEventListener() {
            override fun threadAdded(threadId: String) {
                super.threadAdded(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }

            override fun threadRemoved(threadId: String) {
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
            val personalThreadId = preferenceHelper.personalThreadId
            textile.threads.get(personalThreadId)
            Timber.i("Personal thread has already been created: $personalThreadId")
        } catch (e: Exception) {
            addThread(
                textile.profile.name(),
                Model.Thread.Type.PRIVATE,
                Model.Thread.Sharing.NOT_SHARED
            ).also {
                preferenceHelper.personalThreadId = it.id
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

    fun isSupportedFeedItemType(feedItemData: FeedItemData) =
        feedItemSubtype.contains(feedItemData.type)

    fun listFeeds(threadId: String): ArrayList<FeedItemData> {
        return View.FeedRequest.newBuilder()
            .setThread(threadId)
            .setLimit(REQUEST_LIMIT)
            .build()
            .let { textile.feed.list(it) }
    }

    /**
     * Files
     */

    fun addFile(filePath: String, caption: String, callback: Handlers.BlockHandler) =
        textile.files.addFiles(filePath, preferenceHelper.personalThreadId, caption, callback)

    fun getImageContent(files: View.Files, minWidth: Long = 500, callback: (ByteArray) -> Unit) {
        // imageContentForMinWidth usage: (Textile has not documented)
        // https://github.com/textileio/photos/blob/master/App/Components/authoring-input.tsx#L184
        textile.files.imageContentForMinWidth(
            getFileIpfsPath(files), minWidth, object : Handlers.DataHandler {

                override fun onComplete(data: ByteArray, media: String) =
                    if (media == "image/jpeg" || media == "image/png") callback(data)
                    else Timber.e("Unknown data type: $media")

                override fun onError(e: Exception) {
                    Timber.e("error: get image content callback")
                    Timber.e(e)
                }
            })
    }

    fun getImageContent(ipfsPath: String, minWidth: Long = 500, callback: (ByteArray) -> Unit) {
        // imageContentForMinWidth usage: (Textile has not documented)
        // https://github.com/textileio/photos/blob/master/App/Components/authoring-input.tsx#L184
        textile.files.imageContentForMinWidth(ipfsPath, minWidth, object : Handlers.DataHandler {

            override fun onComplete(data: ByteArray, media: String) =
                if (media == "image/jpeg" || media == "image/png") callback(data)
                else Timber.e("Unknown data type: $media")

            override fun onError(e: java.lang.Exception?) {
                Timber.e("error: get image content callback")
                Timber.e(e)
            }
        })
    }

    fun getFileIpfsPath(files: View.Files): String {
        val fileIndex = getFileIndex(files)
        return "${files.data}/$fileIndex"
    }

    fun getFileIndex(files: View.Files) = files.filesList.let {
        if (it != null && it.size > 0 && it[0].index != 0) it[0].index
        else 0
    }

    fun shareFile(
        dataHash: String,
        caption: String,
        threadId: String,
        callback: (Model.Block) -> Unit
    ) {
        textile.files.shareFiles(dataHash, threadId, caption, object : Handlers.BlockHandler {

            override fun onComplete(block: Model.Block) = callback(block)

            override fun onError(e: java.lang.Exception) {
                Timber.e("error: share file callback")
                Timber.e(e)
            }
        })
    }

    fun ignoreFile(files: View.Files): String = textile.ignores.add(files.block)

    /**
     * Invites
     */

    fun acceptExternalInvite(uri: Uri) {
        val uriWithoutFragment = Uri.parse(uri.toString().replaceFirst('#', '?'))
        safelyInvokeIfNodeOnline {
            val inviteId = uriWithoutFragment.getQueryParameter("id")
            val inviteKey = uriWithoutFragment.getQueryParameter("key")
            if (inviteId.isNullOrEmpty() || inviteKey.isNullOrEmpty()) {
                Timber.e("Cannot parse invite link. ID: $inviteId, Key: $inviteKey")
            } else acceptExternalInvite(inviteId, inviteKey)
        }
    }

    private fun acceptExternalInvite(inviteId: String, key: String): String {
        Timber.i("Accepting external invitation: $inviteId with key $key")
        val blockHash = textile.invites.acceptExternal(inviteId, key)
        if (blockHash.isEmpty()) Timber.i("Already joined the thread.")
        else Timber.i("Invite successful: $blockHash")
        return blockHash
    }

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