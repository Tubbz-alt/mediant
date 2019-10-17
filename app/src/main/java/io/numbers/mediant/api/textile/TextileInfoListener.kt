package io.numbers.mediant.api.textile

import io.textile.pb.Model
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
import timber.log.Timber

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