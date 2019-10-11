package io.numbers.mediant.util

import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile

// Note that the callback function will be executed on a background thread.
fun Textile.safelyInvokeIfNodeOnline(callback: () -> Unit) {
    if (this.isNodeOnline) callback()
    else {
        this.addEventListener(object : BaseTextileEventListener() {
            override fun nodeOnline() {
                super.nodeOnline()
                callback()
            }
        })
    }
}

val Textile.isNodeOnline: Boolean
    get() = try {
        this.online()
    } catch (e: NullPointerException) {
        false
    }