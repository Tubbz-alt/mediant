package io.numbers.mediant.util

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.protobuf.Timestamp
import io.textile.textile.Util
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

enum class ActivityRequestCodes(val value: Int) { CAMERA(0) }

const val generalErrorMessage = "Oh no! Bad things happened!"

data class SnackbarArgs(val message: String, @BaseTransientBottomBar.Duration val duration: Int = Snackbar.LENGTH_LONG) {
    constructor(exception: Exception) : this(exception.message ?: generalErrorMessage)
}

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