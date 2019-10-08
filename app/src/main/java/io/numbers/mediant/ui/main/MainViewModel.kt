package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class MainViewModel : ViewModel() {
    var name = MutableLiveData("a")

    fun onClick() {
        name.value = if (name.value == "a") "b"
        else "a"
        Timber.d("name has changed to: ${name.value}")
    }
}