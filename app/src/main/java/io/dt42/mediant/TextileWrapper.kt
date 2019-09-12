package io.dt42.mediant

import android.content.Context
import android.util.Log
import io.dt42.mediant.model.Post
import io.textile.pb.Model
import io.textile.pb.Model.Thread.Sharing
import io.textile.pb.Model.Thread.Type
import io.textile.pb.QueryOuterClass
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Handlers
import io.textile.textile.Textile
import io.textile.textile.TextileLoggingListener
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "TEXTILE_WRAPPER"

object TextileWrapper {
    val isOnline: Boolean
        get() = try {
            Textile.instance().online()
        } catch (e: NullPointerException) {
            false
        }
    val profileAddress: String
        get() = Textile.instance().profile.get().address

    fun init(context: Context, debug: Boolean) {
        val path = File(context.filesDir, "textile-go").absolutePath
        if (!Textile.isInitialized(path)) {
            val phrase = Textile.initializeCreatingNewWalletAndAccount(path, debug, false)
            Log.i(TAG, "Create new wallet: $phrase")
        }
        Textile.launch(context, path, debug)
        Textile.instance().addEventListener(TextileLoggingListener())
        invokeAfterNodeOnline { initPersonalThread() }
    }

    /*-------------------------------------------
     * Threads
     *-----------------------------------------*/

    private fun initPersonalThread() {
        try {
            getThreadIdByName(profileAddress)
        } catch (e: NoSuchElementException) {
            createThread(profileAddress, Type.PRIVATE, Sharing.NOT_SHARED)
            Log.i(TAG, "Create personal thread: $profileAddress")
        } finally {
            Log.i(TAG, "Personal thread ($profileAddress) has been created.")
        }
    }

    fun logThreads() {
        for (i in 0 until Textile.instance().threads.list().itemsCount) {
            Log.i(TAG, Textile.instance().threads.list().getItems(i).toString())
        }
    }

    private fun createThread(name: String, type: Type, sharing: Sharing) {
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setPreset(View.AddThreadConfig.Schema.Preset.MEDIA)
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey("${BuildConfig.APPLICATION_ID}.${BuildConfig.VERSION_NAME}.$name")
            .setName(name)
            .setType(type)
            .setSharing(sharing)
            .setSchema(schema)
            .build()
        Textile.instance().threads.add(config)
        Log.i(TAG, "Create new thread: $name")
    }

    private fun getThreadIdByName(name: String): String {
        val threadList = Textile.instance().threads.list()
        for (i in 0 until threadList.itemsCount) {
            val mThread = threadList.getItems(i)
            if (name == mThread.name) {
                return mThread.id
            }
        }
        throw NoSuchElementException("Cannot find thread $name")
    }

    private fun addThreadFileByFilePath(filePath: String, threadId: String, caption: String) {
        Textile.instance().files.addFiles(
            filePath,
            threadId,
            caption,
            object : Handlers.BlockHandler {
                override fun onComplete(block: Model.Block?) {
                    Log.i(TAG, "Add file ($filePath) to thread ($threadId) successfully.")
                }

                override fun onError(e: Exception?) {
                    Log.e(TAG, "Add file ($filePath) to thread ($threadId) with error.")
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            })
    }

    /*-------------------------------------------
     * Files
     *-----------------------------------------*/

    fun addImage(filePath: String, threadName: String, caption: String) =
        addThreadFileByFilePath(filePath, getThreadIdByName(threadName), caption)

    suspend fun fetchPosts(threadName: String, limit: Long = 10): MutableList<Post> =
        suspendCoroutine { continuation ->
            val posts = java.util.Collections.synchronizedList(mutableListOf<Post>())
            val hasResumed = AtomicBoolean(false)
            val filesList =
                Textile.instance().files.list(getThreadIdByName(threadName), null, limit)
            Log.d(TAG, "$threadName fetched filesList size: ${filesList.itemsCount}")
            if (filesList.itemsCount == 0) {
                continuation.resume(posts)
            }
            for (i in 0 until filesList.itemsCount) {
                val files = filesList.getItems(i)
                val handler = object : Handlers.DataHandler {
                    override fun onComplete(data: ByteArray?, media: String?) {
                        if (media == "image/jpeg") {
                            posts.add(Post(files.user.name, files.date, data, files.caption))
                        } else {
                            Log.e(TAG, "Unknown media type: $media")
                        }
                        Log.i(TAG, "Posts fetched: ${posts.size} / ${filesList.itemsCount}")
                        if (posts.size == filesList.itemsCount && !hasResumed.get()) {
                            hasResumed.set(true)
                            continuation.resume(posts)
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, Log.getStackTraceString(e))
                        if (!hasResumed.get()) {
                            hasResumed.set(true)
                            // still resume posts though some posts cannot be retrieved
                            continuation.resume(posts)
                        }
                    }
                }

                // TODO: use Textile.instance().files.imageContentForMinWidth() instead
                // Currently, Textile.instance().files.imageContentForMinWidth() only gets null, and
                // I don't know why.
                files.filesList.forEach { file ->
                    file.linksMap["large"]?.hash?.also {
                        Textile.instance().files.content(it, handler)
                    }
                }
            }
        }


    /*-------------------------------------------
     * Contacts
     *-----------------------------------------*/

    fun getContactByName(name: String) {
        val options = QueryOuterClass.QueryOptions.newBuilder()
            .setWait(10)
            .setLimit(1)
            .build()
        val query = QueryOuterClass.ContactQuery.newBuilder()
            .setName(name)
            .build()
        val handle = Textile.instance().contacts.search(query, options)
        Log.i(TAG, "Handle string: $handle")
    }

    /*-------------------------------------------
     * Invites
     *-----------------------------------------*/

    /**
     * Accept invitation sent by Textile Photo
     */
    fun acceptExternalInvitation(inviteId: String, key: String) {
        Log.i(TAG, "Accepting invitation: $inviteId with key $key")
        val newBlockHash = Textile.instance().invites.acceptExternal(inviteId, key)
        Log.i(TAG, "Accepted invitation of thread: $newBlockHash")
    }

    /*-------------------------------------------
     * Utils
     *-----------------------------------------*/

    /**
     * Invoke the callback function after node has online. If the node has already online, the
     *   callback will be invoked immediately.
     * @param callback the callback function
     */
    fun invokeAfterNodeOnline(callback: () -> Unit) {
        if (isOnline) {
            callback.invoke()
        } else {
            Textile.instance().addEventListener(object : BaseTextileEventListener() {
                override fun nodeOnline() {
                    super.nodeOnline()
                    callback.invoke()
                }
            })
        }
    }
}