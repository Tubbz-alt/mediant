package io.numbers.mediant.util

import com.google.protobuf.Timestamp
import io.textile.textile.Util
import java.text.SimpleDateFormat
import java.util.*

fun timestampToString(timestamp: Timestamp): String =
    SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
        Util.timestampToDate(timestamp)
    )