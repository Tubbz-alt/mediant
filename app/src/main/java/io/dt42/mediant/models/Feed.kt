package io.dt42.mediant.models

import com.google.protobuf.Timestamp

data class Feed(
    val username: String,
    val date: Timestamp,
    val data: ByteArray?,
    val caption: String
) : Comparable<Feed> {
    override operator fun compareTo(other: Feed): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}