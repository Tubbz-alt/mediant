package io.dt42.mediant.models

import com.google.protobuf.Timestamp

data class Feed(
    val username: String,
    val date: Timestamp,
    val data: ByteArray?,
    val caption: String
) : Comparable<Feed> {
    override operator fun compareTo(other: Feed): Int {
        if (date.seconds == other.date.seconds && date.nanos == other.date.nanos) return 0

        // For the reverse order feeds in time, the older the feed_personal, the closer to the thread rear.
        if (date.seconds > other.date.seconds || (date.seconds == other.date.seconds && date.nanos > other.date.nanos)) return -1
        return 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feed

        if (username != other.username) return false
        if (date != other.date) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (caption != other.caption) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + caption.hashCode()
        return result
    }
}