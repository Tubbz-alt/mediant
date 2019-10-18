package io.numbers.mediant.util

import com.google.protobuf.Timestamp
import io.textile.textile.Util
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun timestampToString(timestamp: Timestamp): String =
    SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
        Util.timestampToDate(timestamp)
    )

fun File.deleteDirectory(): Boolean {
    return if (exists()) {
        listFiles()?.forEach {
            if (it.isDirectory) it.deleteDirectory()
            else it.delete()
        }
        delete()
    } else false
}