package io.dt42.mediant.ui.main.model

data class Post(val username: String, val data: ByteArray?, val description: String) {
    constructor(username: String, description: String) : this(username, null, description)
}