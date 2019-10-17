package io.numbers.mediant.api.textile

import io.textile.textile.FeedItemData

fun FeedItemData.hasSameContentsTo(feedItemData: FeedItemData): Boolean {
    return this.type == feedItemData.type && this.block == feedItemData.block
            && this.text == feedItemData.text && this.comment == feedItemData.comment
            && this.like == feedItemData.like && this.files == feedItemData.files
            && this.ignore == feedItemData.ignore && this.join == feedItemData.join
            && this.leave == feedItemData.leave && this.announce == feedItemData.announce
}