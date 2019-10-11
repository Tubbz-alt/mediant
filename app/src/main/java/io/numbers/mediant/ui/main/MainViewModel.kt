package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    val userName = MutableLiveData("your name22")

    fun onCreateAccount() = Timber.d(userName.value)
}