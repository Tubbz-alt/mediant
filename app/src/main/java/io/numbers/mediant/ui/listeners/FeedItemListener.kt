package io.numbers.mediant.ui.listeners

import io.textile.textile.FeedItemData

interface FeedItemListener {

    fun onShowProof(feedItemData: FeedItemData)

    fun onPublish(feedItemData: FeedItemData)

    fun onDelete(feedItemData: FeedItemData)
}